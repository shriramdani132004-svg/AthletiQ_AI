package com.athletiq.backend.application.dto;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long eventId,
        Long formVersionId,
        Long applicantId,
        LocalDateTime submittedAt
) {
}