package com.athletiq.backend.application.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "player_response_tokens",
        indexes = {
                @Index(
                        name = "idx_player_response_token_hash",
                        columnList = "token_hash",
                        unique = true
                ),
                @Index(
                        name = "idx_player_response_token_application",
                        columnList = "application_id"
                )
        }
)
public class PlayerResponseToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "application_id",
            nullable = false
    )
    private Long applicationId;

    @Column(
            name = "event_id",
            nullable = false
    )
    private Long eventId;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    protected PlayerResponseToken() {
    }

    public PlayerResponseToken(
            Long applicationId,
            Long eventId,
            String tokenHash,
            LocalDateTime expiresAt
    ) {
        this.applicationId = applicationId;
        this.eventId = eventId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(
            LocalDateTime usedAt
    ) {
        this.usedAt = usedAt;
    }
}