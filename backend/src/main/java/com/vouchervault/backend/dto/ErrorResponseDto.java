package com.vouchervault.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private String errorCode;
    private String message;
    private String description;
    private LocalDateTime timestamp;
    private String apiPath;
}
