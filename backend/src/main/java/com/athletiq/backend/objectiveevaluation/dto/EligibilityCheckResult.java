package com.athletiq.backend.objectiveevaluation.dto;

public record EligibilityCheckResult(

        String requirement,

        boolean required,

        boolean passed,

        String candidateValue,

        String requiredValue,

        String explanation

) {
}