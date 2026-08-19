package com.athletiq.backend.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.PlayerResponseStatus;
import com.athletiq.backend.application.entity.PlayerResponseToken;
import com.athletiq.backend.application.entity.SelectionStatus;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.application.repository.PlayerResponseTokenRepository;

@Service
public class PlayerResponseService {

    private final ApplicationRepository applicationRepository;
    private final PlayerResponseTokenRepository tokenRepository;
    private final PlayerResponseTokenService tokenService;

    public PlayerResponseService(
            ApplicationRepository applicationRepository,
            PlayerResponseTokenRepository tokenRepository,
            PlayerResponseTokenService tokenService
    ) {
        this.applicationRepository = applicationRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public Map<String, Object> respond(
            String rawToken,
            PlayerResponseStatus responseStatus
    ) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Response token is required."
            );
        }

        if (responseStatus != PlayerResponseStatus.ACCEPTED &&
                responseStatus != PlayerResponseStatus.DECLINED) {
            throw new IllegalArgumentException(
                    "Invalid player response."
            );
        }

        PlayerResponseToken token =
                tokenRepository.findByTokenHash(
                        tokenService.hashToken(rawToken)
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid response token."
                        )
                );

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "This response link has expired."
            );
        }

        Application application =
                applicationRepository.findById(
                        token.getApplicationId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found."
                        )
                );

        if (application.getEvent() == null ||
                !token.getEventId().equals(
                        application.getEvent().getId()
                )) {
            throw new IllegalArgumentException(
                    "Invalid event response token."
            );
        }

        if (application.getSelectionStatus() !=
                SelectionStatus.SELECTED) {
            throw new IllegalStateException(
                    "This application is not currently selected."
            );
        }

        application.setPlayerResponseStatus(responseStatus);
        application.setPlayerRespondedAt(LocalDateTime.now());

        applicationRepository.save(application);

        return Map.of(
                "applicationId",
                application.getId(),
                "response",
                responseStatus.name(),
                "message",
                responseStatus == PlayerResponseStatus.ACCEPTED
                        ? "Participation accepted."
                        : "Participation declined."
        );
    }
}