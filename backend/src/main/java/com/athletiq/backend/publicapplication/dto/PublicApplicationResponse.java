package com.athletiq.backend.publicapplication.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PublicApplicationResponse(
        Long eventId,
        String eventName,
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
        Long formVersionId,
        Integer formVersionNumber,
        List<PublicFormFieldResponse> fields
) {
}