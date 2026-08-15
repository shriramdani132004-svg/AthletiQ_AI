package com.athletiq.backend.publicapplication.dto;

import java.time.LocalDateTime;

public record PublicApplicationSubmitResponse(
        Long applicationId,
        Long eventId,
        Long formVersionId,
        LocalDateTime submittedAt
) {
}