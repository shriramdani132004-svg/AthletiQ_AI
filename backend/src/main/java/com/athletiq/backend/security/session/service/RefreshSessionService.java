package com.athletiq.backend.security.session.service;

import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.jwt.JwtService;
import com.athletiq.backend.security.session.entity.RefreshSession;
import com.athletiq.backend.security.session.repository.RefreshSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class RefreshSessionService {

    private static final long REFRESH_TOKEN_DAYS = 30;

    private final RefreshSessionRepository refreshSessionRepository;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshSessionService(
            RefreshSessionRepository refreshSessionRepository,
            JwtService jwtService
    ) {
        this.refreshSessionRepository = refreshSessionRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public String createRefreshToken(User user) {

        String rawToken = generateSecureToken();

        RefreshSession session = new RefreshSession();
        session.setUser(user);
        session.setTokenHash(hashToken(rawToken));
        session.setExpiresAt(
                Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS)
        );
        session.setRevoked(false);

        refreshSessionRepository.save(session);

        return rawToken;
    }

    @Transactional
    public String rotateAccessToken(String rawRefreshToken) {

        String tokenHash = hashToken(rawRefreshToken);

        RefreshSession oldSession =
                refreshSessionRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid refresh token"
                                )
                        );

        if (oldSession.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh session has been revoked"
            );
        }

        if (oldSession.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        User user = oldSession.getUser();

        oldSession.setRevoked(true);
        refreshSessionRepository.save(oldSession);

        return jwtService.generateAccessToken(user);
    }

    @Transactional
    public void revoke(String rawRefreshToken) {

        String tokenHash = hashToken(rawRefreshToken);

        refreshSessionRepository.findByTokenHash(tokenHash)
                .ifPresent(session -> {
                    session.setRevoked(true);
                    refreshSessionRepository.save(session);
                });
    }

    private String generateSecureToken() {

        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable",
                    exception
            );
        }
    }
}
