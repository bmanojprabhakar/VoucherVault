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
public class ApiResponseDto<T> {
    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
