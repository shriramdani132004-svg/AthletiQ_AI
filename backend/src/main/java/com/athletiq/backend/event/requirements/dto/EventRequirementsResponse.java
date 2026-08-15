package com.athletiq.backend.event.requirements.dto;

import java.time.LocalDateTime;

public record EventRequirementsResponse(
        Long id,
        Long eventId,
        String requiredPositions,
        Integer minAge,
        Integer maxAge,
        String minimumExperience,
        String requiredAchievements,
        String requiredSkills,
        String performanceRequirements,
        String fitnessRequirements,
        String availabilityRequirements,
        String eligibilityConditions,
        String eventSpecificRequirements,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}