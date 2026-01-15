package com.vouchervault.backend.exception;

import com.vouchervault.backend.utils.AppConstants;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String detail) {
        super(String.format(AppConstants.AUTHENTICATION_FAILED, detail),
                AppConstants.ERROR_CODE_UNAUTHORIZED);
    }
}
