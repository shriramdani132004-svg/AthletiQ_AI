package com.athletiq.backend.application.dto;

import com.athletiq.backend.application.entity.ApplicationStatus;

import java.time.LocalDateTime;

public record OrganizerApplicationDetailResponse(

        Long applicationId,

        Long eventId,

        Long formVersionId,

        Integer formVersionNumber,

        String playerName,

        String email,

        String phone,

        Integer age,

        String position,

        String experience,

        String achievements,

        String skills,

        String sportsInformation,

        String submittedData,

        String fileMetadata,

        java.math.BigDecimal score,

        Integer ranking,

        ApplicationStatus status,

        LocalDateTime submittedAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}