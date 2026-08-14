package com.athletiq.backend.security.verification;

import java.time.Instant;

public class VerificationToken {
    private String token;
    private String email;
    private Instant expiresAt;
    private VerificationStatus status;

    public VerificationToken() {
    }

    public VerificationToken(String token, String email, Instant expiresAt) {
        this.token = token;
        this.email = email;
        this.expiresAt = expiresAt;
        this.status = VerificationStatus.PENDING;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }
    public boolean isExpired() { return expiresAt != null && Instant.now().isAfter(expiresAt); }
}