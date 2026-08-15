package com.athletiq.backend.publicapplication.dto;

public record PublicApplicationLinkResponse(
        Long id,
        Long eventId,
        Long formVersionId,
        Integer formVersionNumber,
        String publicCode,
        String publicUrl,
        boolean active
) {
}