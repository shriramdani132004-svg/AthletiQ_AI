package com.athletiq.backend.profile.dto;

public record OrganizationInfoResponse(
        Long userId,
        String organizationName,
        String organizationDescription
) {
}