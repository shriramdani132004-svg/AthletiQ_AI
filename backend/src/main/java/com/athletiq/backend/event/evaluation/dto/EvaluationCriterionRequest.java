package com.athletiq.backend.event.evaluation.dto;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EvaluationCriterionRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("100.00")
        BigDecimal weight,

        @NotNull
        BigDecimal minScore,

        @NotNull
        BigDecimal maxScore,

        @NotNull
        EvaluationCriterionType criterionType,

        Boolean enabled,

        @NotNull
        @Min(0)
        Integer displayOrder
) {
}