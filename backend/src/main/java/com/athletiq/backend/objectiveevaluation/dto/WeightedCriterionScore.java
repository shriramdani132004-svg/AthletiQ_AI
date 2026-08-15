package com.athletiq.backend.objectiveevaluation.dto;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;

import java.math.BigDecimal;

public record WeightedCriterionScore(

        Long criterionId,

        String criterionName,

        EvaluationCriterionType criterionType,

        BigDecimal normalizedScore,

        BigDecimal weight,

        BigDecimal weightedContribution,

        boolean valid,

        String explanation

) {
}