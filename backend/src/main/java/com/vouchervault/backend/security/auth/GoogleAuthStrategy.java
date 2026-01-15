package com.vouchervault.backend.security.auth;

import com.vouchervault.backend.exception.AuthenticationException;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import com.vouchervault.backend.utils.AppConstants;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Override
    public String authenticate(String code) {
        log.debug(AppConstants.LOG_ENTRY, "authenticate", AppConstants.GOOGLE_PROVIDER);
        try {
            // 1. Exchange Code for Access Token
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add(AppConstants.GOOGLE_CODE_KEY, code);
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("grant_type", "authorization_code");

            JsonNode tokenResponse = restClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (tokenResponse == null || !tokenResponse.has(AppConstants.GOOGLE_TOKEN_KEY)) {
                throw new AuthenticationException("Failed to obtain access token from Google");
            }

            String accessToken = tokenResponse.get(AppConstants.GOOGLE_TOKEN_KEY).asText();

            // 2. Get User Info
            JsonNode userInfo = restClient.get()
                    .uri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(JsonNode.class);

            if (userInfo == null) {
                throw new AuthenticationException("Failed to obtain user info from Google");
            }

            String email = userInfo.get("email").asText();
            String socialId = userInfo.get("sub").asText();

            String fullName = extractName(userInfo, email);

            // 3. Upsert User
            User user = userRepository.findBySocialId(socialId)
                    .map(existingUser -> {
                        existingUser.setName(fullName);
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setSocialId(socialId);
                        newUser.setName(fullName);
                        return userRepository.save(newUser);
                    });

            log.info(AppConstants.LOG_INFO, "User " + email + " authenticated via Google");

            // 4. Generate App JWT
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            log.debug(AppConstants.LOG_EXIT, "authenticate");
            return token;

        } catch (Exception e) {
            log.error(AppConstants.LOG_ERROR, "Google authentication", e.getMessage());
            throw new AuthenticationException(e.getMessage());
        }
    }

    private String extractName(JsonNode userInfo, String email) {
        String fullName = "";
        String givenName = userInfo.has("given_name") ? userInfo.get("given_name").asText() : "";
        String familyName = userInfo.has("family_name") ? userInfo.get("family_name").asText() : "";

        if (!givenName.isEmpty()) {
            fullName = givenName + (familyName.isEmpty() ? "" : " " + familyName);
        } else if (userInfo.has("name")) {
            fullName = userInfo.get("name").asText();
        } else {
            fullName = email.split("@")[0];
        }
        return fullName;
    }

    @Override
    public String getProviderName() {
        return AppConstants.GOOGLE_PROVIDER;
    }
}
