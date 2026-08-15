package com.athletiq.backend.objectiveevaluation.dto;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;

import java.math.BigDecimal;

public record CriterionValueExtraction(

        Long criterionId,

        String criterionName,

        EvaluationCriterionType criterionType,

        BigDecimal weight,

        BigDecimal minScore,

        BigDecimal maxScore,

        boolean enabled,

        boolean mapped,

        String sourceFieldKey,

        String originalValue,

        Object normalizedValue,

        String explanation

) {
}