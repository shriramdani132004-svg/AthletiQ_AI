package com.athletiq.backend.security.passwordreset;

public record PasswordResetConfirmRequest(String token, String newPassword) {}
