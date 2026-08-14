package com.athletiq.backend.security.auth.exception;

public class AuthenticationFailureException extends RuntimeException {

    public AuthenticationFailureException(String message) {
        super(message);
    }
}
