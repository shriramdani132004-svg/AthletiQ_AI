package com.athletiq.backend.profile.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePhotoUrl,
        String organizationName,
        String organizationDescription
        ) {
}