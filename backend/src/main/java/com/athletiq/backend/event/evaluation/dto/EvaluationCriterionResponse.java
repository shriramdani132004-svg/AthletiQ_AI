package com.athletiq.backend.event.evaluation.dto;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EvaluationCriterionResponse(
        Long id,
        Long eventId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal minScore,
        BigDecimal maxScore,
        EvaluationCriterionType criterionType,
        boolean enabled,
        Integer displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}