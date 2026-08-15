package com.athletiq.backend.objectiveevaluation.dto;

import com.athletiq.backend.objectiveevaluation.entity.EligibilityStatus;
import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ObjectiveEvaluationResponse(

        Long evaluationId,

        Long applicationId,

        Long eventId,

        Long formVersionId,

        ObjectiveEvaluationStatus status,

        EligibilityStatus eligibilityStatus,

        BigDecimal objectiveScore,

        String eligibilityExplanation,

        String scoreExplanation,

        Integer calculationVersion,

        LocalDateTime calculatedAt,

        List<CriterionScoreExplanation> criteria

) {
}