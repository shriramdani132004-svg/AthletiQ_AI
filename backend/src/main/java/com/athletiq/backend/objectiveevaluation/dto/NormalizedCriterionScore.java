package com.athletiq.backend.objectiveevaluation.dto;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;

import java.math.BigDecimal;

public record NormalizedCriterionScore(

        Long criterionId,

        String criterionName,

        EvaluationCriterionType criterionType,

        BigDecimal rawNumericValue,

        BigDecimal minScore,

        BigDecimal maxScore,

        BigDecimal normalizedScore,

        boolean valid,

        String explanation

) {
}