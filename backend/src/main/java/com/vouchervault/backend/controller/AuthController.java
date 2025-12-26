package com.vouchervault.backend.controller;

import com.vouchervault.backend.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            String token = googleAuthService.authenticateGoogle(code);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
