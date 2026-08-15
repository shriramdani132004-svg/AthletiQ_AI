package com.athletiq.backend.application.dto;

import java.time.LocalDateTime;

public record OrganizerApplicationResponse(
        Long id,
        Long eventId,
        Long formVersionId,
        Integer formVersionNumber,
        Long applicantId,
        String submittedData,
        LocalDateTime submittedAt
) {
}