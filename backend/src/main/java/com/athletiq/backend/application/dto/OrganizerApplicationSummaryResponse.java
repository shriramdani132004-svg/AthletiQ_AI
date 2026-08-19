package com.athletiq.backend.application.dto;

import com.athletiq.backend.application.entity.ApplicationStatus;

import java.time.LocalDateTime;

public record OrganizerApplicationSummaryResponse(

        Long applicationId,

        Long eventId,

        String playerName,

        String email,

        String phone,

        Integer age,

        String position,

        java.math.BigDecimal score,

        Integer ranking,

        ApplicationStatus status,

        LocalDateTime applicationDate,

        Long formVersionId,

        Double aiScore,

        String aiEvaluationStatus

) {
}