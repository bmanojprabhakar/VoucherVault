package com.vouchervault.backend.security.auth;

import com.vouchervault.backend.exception.AuthenticationException;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import com.vouchervault.backend.utils.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoogleAuthStrategyTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private GoogleAuthStrategy googleAuthStrategy;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthStrategy, "clientId", "test-client-id");
        ReflectionTestUtils.setField(googleAuthStrategy, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(googleAuthStrategy, "redirectUri", "test-redirect-uri");
    }

    @Test
    void getProviderName_Success() {
        assertEquals(AppConstants.GOOGLE_PROVIDER, googleAuthStrategy.getProviderName());
    }

    @Test
    void authenticate_Failure_InvalidCode() {
        // Since we can't easily mock RestClient in unit tests without complex setup,
        // we test the exception handling when restClient fails.
        // In a real scenario, we'd use MockWebServer or mock the RestClient.Builder.
        assertThrows(AuthenticationException.class, () -> googleAuthStrategy.authenticate("invalid-code"));
    }
}
