package com.athletiq.backend.profile.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}