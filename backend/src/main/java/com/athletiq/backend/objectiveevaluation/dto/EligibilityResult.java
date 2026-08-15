package com.athletiq.backend.objectiveevaluation.dto;

import java.util.List;

public record EligibilityResult(

        boolean eligible,

        List<EligibilityCheckResult> checks,

        List<String> failedRequirements,

        List<String> passedRequirements,

        String explanation

) {
}