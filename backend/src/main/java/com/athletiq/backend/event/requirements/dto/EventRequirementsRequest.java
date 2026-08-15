package com.athletiq.backend.event.requirements.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record EventRequirementsRequest(

        @Size(max = 2000)
        String requiredPositions,

        @Min(0)
        @Max(120)
        Integer minAge,

        @Min(0)
        @Max(120)
        Integer maxAge,

        @Size(max = 2000)
        String minimumExperience,

        @Size(max = 5000)
        String requiredAchievements,

        @Size(max = 5000)
        String requiredSkills,

        @Size(max = 5000)
        String performanceRequirements,

        @Size(max = 5000)
        String fitnessRequirements,

        @Size(max = 5000)
        String availabilityRequirements,

        @Size(max = 5000)
        String eligibilityConditions,

        @Size(max = 5000)
        String eventSpecificRequirements
) {
}