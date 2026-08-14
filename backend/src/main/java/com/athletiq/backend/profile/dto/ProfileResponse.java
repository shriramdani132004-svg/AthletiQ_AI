package com.athletiq.backend.profile.dto;

public record ProfileResponse(
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePhotoUrl,
        String organizationName,
        String organizationDescription
        ) {
}