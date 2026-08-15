package com.athletiq.backend.event.evaluation.dto;

import java.math.BigDecimal;

public record EvaluationCriteriaValidationResponse(
        boolean valid,
        BigDecimal activeWeightTotal,
        String message
) {
}