package com.athletiq.backend.security.ownership;

public class SecurityAccessDeniedException extends RuntimeException {

    public SecurityAccessDeniedException(String message) {
        super(message);
    }

    public SecurityAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}