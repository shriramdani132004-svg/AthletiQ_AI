package com.athletiq.backend.security.auth.dto;

import com.athletiq.backend.security.auth.entity.Role;
import com.athletiq.backend.security.auth.entity.User;

public record RegisterResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role,
        boolean emailVerified
) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isEmailVerified()
        );
    }
}
