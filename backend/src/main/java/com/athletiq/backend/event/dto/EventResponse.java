package com.athletiq.backend.event.dto;

import com.athletiq.backend.event.entity.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        Long organizerId,
        String name,
        String sport,
        String description,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime registrationDeadline,
        Integer playersRequired,
        String ageCategory,
        String eligibilityCriteria,
        String eventRules,
        String bannerUrl,
        EventStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}