package com.athletiq.backend.objectiveevaluation.dto;

import java.math.BigDecimal;
import java.util.List;

public record ObjectiveScoreExplanation(

        Long applicationId,

        Long eventId,

        BigDecimal objectiveScore,

        BigDecimal totalWeight,

        List<CriterionScoreExplanation> criteria,

        String summary

) {
}