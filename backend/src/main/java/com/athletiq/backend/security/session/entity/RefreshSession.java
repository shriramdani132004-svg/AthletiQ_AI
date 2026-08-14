package com.athletiq.backend.security.session.entity;

import com.athletiq.backend.security.auth.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "refresh_sessions",
    indexes = {
        @Index(name = "idx_refresh_sessions_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_refresh_sessions_user_id", columnList = "user_id")
    }
)
public class RefreshSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public RefreshSession() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;

        if (revoked && revokedAt == null) {
            revokedAt = Instant.now();
        }
    }
}
