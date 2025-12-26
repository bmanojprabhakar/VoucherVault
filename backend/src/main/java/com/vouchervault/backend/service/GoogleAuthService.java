package com.vouchervault.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public String authenticateGoogle(String code) {
        // 1. Exchange Code for Access Token
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
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

        String accessToken = tokenResponse.get("access_token").asText();

        // 2. Get User Info
        JsonNode userInfo = restClient.get()
                .uri("https://www.googleapis.com/oauth2/v3/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(JsonNode.class);

        String email = userInfo.get("email").asText();
        String socialId = userInfo.get("sub").asText();

        // Robust Name Extraction
        String givenName = userInfo.has("given_name") ? userInfo.get("given_name").asText() : "";
        String familyName = userInfo.has("family_name") ? userInfo.get("family_name").asText() : "";

        String fullName;
        if (!givenName.isEmpty()) {
            fullName = givenName + (familyName.isEmpty() ? "" : " " + familyName);
        } else if (userInfo.has("name")) {
            fullName = userInfo.get("name").asText();
        } else {
            fullName = email.split("@")[0];
        }

        String finalName = fullName; // strictly for lambda use if needed

        // 3. Upsert User
        User user = userRepository.findBySocialId(socialId)
                .map(existingUser -> {
                    existingUser.setName(finalName);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setSocialId(socialId);
                    newUser.setName(finalName);
                    return userRepository.save(newUser);
                });

        // 4. Generate App JWT
        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }
}
