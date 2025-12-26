package com.vouchervault.backend.controller;

import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Profile("dev") // This controller only exists in the 'dev' profile
public class DevAuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/dev-login")
    public ResponseEntity<Map<String, String>> devLogin() {
        String devSocialId = "dev-user-123";

        // Find or Create Dev User
        User user = userRepository.findBySocialId(devSocialId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setSocialId(devSocialId);
                    newUser.setEmail("dev@localhost");
                    newUser.setName("Dev Developer");
                    return userRepository.save(newUser);
                });

        // Generate valid JWT for this user
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
