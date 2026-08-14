package com.athletiq.backend.security.session.dto;

public record RefreshTokenResponse(
        String accessToken,
        String tokenType
) {
}
