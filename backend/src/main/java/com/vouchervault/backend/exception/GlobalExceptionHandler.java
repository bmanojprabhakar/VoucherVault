package com.vouchervault.backend.exception;

import com.vouchervault.backend.dto.ErrorResponseDto;
import com.vouchervault.backend.utils.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.error("BaseException occurred: {} | Error Code: {}", ex.getMessage(), ex.getErrorCode());
        HttpStatus status = switch (ex.getErrorCode()) {
            case AppConstants.ERROR_CODE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case AppConstants.ERROR_CODE_UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case AppConstants.ERROR_CODE_FORBIDDEN -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };

        return new ResponseEntity<>(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI()),
                status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", ex);
        return new ResponseEntity<>(buildErrorResponse(
                AppConstants.ERROR_CODE_INTERNAL_SERVER,
                AppConstants.INTERNAL_SERVER_ERROR,
                request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseDto buildErrorResponse(String errorCode, String message, String path) {
        return ErrorResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .description("Please check the API documentation for more details.")
                .timestamp(LocalDateTime.now())
                .apiPath(path)
                .build();
    }
}
