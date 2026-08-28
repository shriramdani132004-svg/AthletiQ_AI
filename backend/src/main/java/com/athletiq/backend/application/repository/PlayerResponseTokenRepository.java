package com.athletiq.backend.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.athletiq.backend.application.entity.PlayerResponseToken;

public interface PlayerResponseTokenRepository
        extends JpaRepository<
        PlayerResponseToken,
        Long
        > {

    Optional<PlayerResponseToken>
    findByTokenHash(String tokenHash);
        long deleteByApplicationId(Long applicationId);
    Optional<PlayerResponseToken>
    findFirstByApplicationIdAndUsedAtIsNullOrderByExpiresAtDesc(
            Long applicationId
    );
}