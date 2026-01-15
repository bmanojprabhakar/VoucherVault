package com.vouchervault.backend.controller;

import com.vouchervault.backend.dto.ApiResponseDto;
import com.vouchervault.backend.security.auth.AuthStrategyFactory;
import com.vouchervault.backend.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.API_V1_AUTH)
@RequiredArgsConstructor
@Slf4j
@Tag(name = AppConstants.SWAGGER_AUTH_TAG, description = AppConstants.SWAGGER_AUTH_DESC)
public class AuthController {

    private final AuthStrategyFactory authStrategyFactory;

    @PostMapping("/{provider}")
    @Operation(summary = "Authenticate user", description = "Authenticates user using social login provider and returns a JWT token")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> authenticate(
            @Parameter(description = "Social login provider (e.g., google, facebook, github)") @PathVariable String provider,
            @RequestBody Map<String, String> payload) {
        log.debug(AppConstants.LOG_ENTRY, "authenticate", provider);
        String code = payload.get(AppConstants.GOOGLE_CODE_KEY);
        String token = authStrategyFactory.getStrategy(provider).authenticate(code);

        ApiResponseDto<Map<String, String>> response = ApiResponseDto.<Map<String, String>>builder()
                .status("SUCCESS")
                .message(AppConstants.AUTH_SUCCESS)
                .data(Map.of(AppConstants.GOOGLE_TOKEN, token))
                .timestamp(LocalDateTime.now())
                .build();

        log.info(AppConstants.LOG_INFO, "User authenticated via " + provider);
        log.debug(AppConstants.LOG_EXIT, "authenticate");
        return ResponseEntity.ok(response);
    }
}
