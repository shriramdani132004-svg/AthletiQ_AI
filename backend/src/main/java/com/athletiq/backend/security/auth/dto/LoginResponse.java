package com.athletiq.backend.security.auth.dto;

import com.athletiq.backend.security.auth.entity.Role;

public record LoginResponse(
        Long userId,
        String email,
        String firstName,
        String lastName,
        Role role,
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
