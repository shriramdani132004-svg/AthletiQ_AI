package com.athletiq.backend.objectiveevaluation.dto;

public record NormalizedAnswer(
        String fieldKey,
        String fieldType,
        String originalValue,
        Object normalizedValue,
        boolean present
) {
}