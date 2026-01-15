package com.vouchervault.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String jwtSecret = "my-test-jwt-secret-which-is-at-least-32-characters-long!!";
    private final long jwtExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", jwtExpirationMs);
        jwtUtil.init();
    }

    @Test
    void generateAndValidateToken_Success() {
        UUID userId = UUID.randomUUID();
        String email = "test@vouchervault.com";

        String token = jwtUtil.generateToken(userId, email);
        assertNotNull(token);

        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId.toString(), jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void validateInvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void validateExpiredToken_ReturnsFalse() {
        // Set short expiration
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -1000L);
        jwtUtil.init();

        UUID userId = UUID.randomUUID();
        String token = jwtUtil.generateToken(userId, "test@vouchervault.com");

        assertFalse(jwtUtil.validateToken(token));
    }
}
