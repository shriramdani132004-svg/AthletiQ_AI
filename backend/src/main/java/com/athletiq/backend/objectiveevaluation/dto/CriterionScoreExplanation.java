package com.athletiq.backend.objectiveevaluation.dto;

import java.math.BigDecimal;

public record CriterionScoreExplanation(

        Long criterionId,

        String criterionName,

        String criterionType,

        String sourceFieldKey,

        String rawValue,

        BigDecimal normalizedScore,

        BigDecimal weight,

        BigDecimal weightedContribution,

        String evidence,

        String explanation

) {
}