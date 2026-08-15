package com.athletiq.backend.exception;

import com.athletiq.backend.security.auth.exception.AuthenticationFailureException;
import com.athletiq.backend.security.ownership.SecurityAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationFailureException.class)
    public ResponseEntity<ApiError> handleAuthenticationFailure(
            AuthenticationFailureException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(SecurityAccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            SecurityAccessDeniedException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of(
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "An unexpected error occurred",
                        request.getRequestURI()
                ));
    }
}