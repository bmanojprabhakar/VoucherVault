package com.vouchervault.backend.controller;

import com.vouchervault.backend.dto.ApiResponseDto;
import com.vouchervault.backend.exception.ResourceNotFoundException;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_USER)
@RequiredArgsConstructor
@Slf4j
@Tag(name = AppConstants.SWAGGER_USER_TAG, description = AppConstants.SWAGGER_USER_DESC)
public class UserController {

        private final UserRepository userRepository;

        @GetMapping("/me")
        @Operation(summary = "Get current user profile", description = "Fetches the profile details of the authenticated user")
        public ResponseEntity<ApiResponseDto<Map<String, String>>> getCurrentUser(
                        @AuthenticationPrincipal String userId) {
                log.debug(AppConstants.LOG_ENTRY, "getCurrentUser", userId);
                User user = userRepository.findById(UUID.fromString(userId))
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                log.info(AppConstants.LOG_INFO, "User details fetched for: " + user.getEmail());

                ApiResponseDto<Map<String, String>> response = ApiResponseDto.<Map<String, String>>builder()
                                .status("SUCCESS")
                                .message("User profile fetched successfully")
                                .data(Map.of(
                                                "id", user.getId().toString(),
                                                "email", user.getEmail(),
                                                "name", user.getName()))
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug(AppConstants.LOG_EXIT, "getCurrentUser");
                return ResponseEntity.ok(response);
        }
}
