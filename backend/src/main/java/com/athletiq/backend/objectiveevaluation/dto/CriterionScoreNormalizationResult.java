package com.athletiq.backend.objectiveevaluation.dto;

import java.math.BigDecimal;
import java.util.List;

public record CriterionScoreNormalizationResult(

        Long applicationId,

        Long eventId,

        List<NormalizedCriterionScore> criteria,

        BigDecimal averageNormalizedScore

) {
}