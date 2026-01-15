package com.vouchervault.backend.security.auth;

public interface AuthStrategy {
    String authenticate(String code);

    String getProviderName();
}
