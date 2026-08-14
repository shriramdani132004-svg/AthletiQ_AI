package com.athletiq.backend.profile.dto;

public record OrganizationInfoRequest(
        String organizationName,
        String organizationDescription
) {
}