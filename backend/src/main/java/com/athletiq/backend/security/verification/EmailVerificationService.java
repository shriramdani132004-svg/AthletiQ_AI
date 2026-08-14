package com.athletiq.backend.security.verification;

public interface EmailVerificationService {

    void sendVerification(String userId, String email);

    boolean verify(String token);
    void sendResendVerification(String userId, String email);
}
