package com.athletiq.backend.application.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.PlayerResponseToken;
import com.athletiq.backend.application.repository.PlayerResponseTokenRepository;

@Service
public class PlayerResponseTokenService {

    private final PlayerResponseTokenRepository tokenRepository;
    private final SecureRandom secureRandom =
            new SecureRandom();

    public PlayerResponseTokenService(
            PlayerResponseTokenRepository tokenRepository
    ) {
        this.tokenRepository =
                tokenRepository;
    }

    @Transactional
    public String createToken(
            Application application
    ) {
        if (application == null ||
                application.getId() == null ||
                application.getEvent() == null ||
                application.getEvent().getId() == null) {
            throw new IllegalArgumentException(
                    "Application and event are required."
            );
        }

        byte[] randomBytes =
                new byte[32];

        secureRandom.nextBytes(
                randomBytes
        );

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                randomBytes
                        );

        String tokenHash =
                hashToken(rawToken);

        PlayerResponseToken token =
                new PlayerResponseToken(
                        application.getId(),
                        application.getEvent().getId(),
                        tokenHash,
                        LocalDateTime.now()
                                .plusDays(7)
                );

        tokenRepository.save(token);

        return rawToken;
    }

    public String hashToken(
            String rawToken
    ) {
        if (rawToken == null ||
                rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Response token is required."
            );
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte value : hash) {
                result.append(
                        String.format(
                                "%02x",
                                value
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to hash response token.",
                    exception
            );
        }
    }
}