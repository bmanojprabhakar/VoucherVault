package com.vouchervault.backend.security.auth;

import com.vouchervault.backend.exception.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthStrategyFactory {

    private final Map<String, AuthStrategy> strategies = new HashMap<>();

    public AuthStrategyFactory(List<AuthStrategy> authStrategies) {
        authStrategies.forEach(strategy -> strategies.put(strategy.getProviderName(), strategy));
    }

    public AuthStrategy getStrategy(String provider) {
        return Optional.ofNullable(strategies.get(provider.toLowerCase()))
                .orElseThrow(() -> new AuthenticationException("Unsupported authentication provider: " + provider));
    }
}
