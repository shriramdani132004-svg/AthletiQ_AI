package com.athletiq.backend.objectiveevaluation.dto;

import java.math.BigDecimal;
import java.util.List;

public record WeightedObjectiveScoreResult(

        Long applicationId,

        Long eventId,

        BigDecimal totalWeight,

        BigDecimal objectiveScore,

        List<WeightedCriterionScore> criteria,

        boolean valid,

        String explanation

) {
}