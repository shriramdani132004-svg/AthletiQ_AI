package com.athletiq.backend.ai.phase11;

import java.util.List;

public record AiEvaluationResult(
        String candidateReference,
        Double score,
        String assessment,
        List<String> strengths,
        List<String> weaknesses,
        String experienceAnalysis,
        List<Object> requirementFit,
        String positionSuitability,
        String recommendation,
        List<Object> concerns,
        String explanation
) {}