package com.athletiq.backend.ai.phase11;

public record AiEvaluationProviderResult(
        String rawResponse,
        String provider,
        String model
) {}