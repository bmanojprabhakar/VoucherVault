package com.vouchervault.backend.security.auth;

import com.vouchervault.backend.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthStrategyFactoryTest {

    private AuthStrategy googleStrategy;
    private AuthStrategyFactory factory;

    @BeforeEach
    void setUp() {
        googleStrategy = mock(AuthStrategy.class);
        when(googleStrategy.getProviderName()).thenReturn("google");
        factory = new AuthStrategyFactory(List.of(googleStrategy));
    }

    @Test
    void getStrategy_Success() {
        AuthStrategy result = factory.getStrategy("google");
        assertEquals(googleStrategy, result);
    }

    @Test
    void getStrategy_CaseInsensitive_Success() {
        AuthStrategy result = factory.getStrategy("GOOGLE");
        assertEquals(googleStrategy, result);
    }

    @Test
    void getStrategy_UnsupportedProvider_ThrowsException() {
        assertThrows(AuthenticationException.class, () -> factory.getStrategy("facebook"));
    }
}
