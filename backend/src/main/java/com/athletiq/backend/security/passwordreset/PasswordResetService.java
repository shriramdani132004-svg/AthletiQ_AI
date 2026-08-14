package com.athletiq.backend.security.passwordreset;

public interface PasswordResetService {
    void requestReset(String email);
    boolean resetPassword(String token, String newPassword);
}
