package com.athletiq.backend.ai.phase11;

public interface AiEvaluationProvider {

    AiEvaluationProviderResult evaluate(
            AiEvaluationProviderRequest request
    );

    default String providerName() {
        return "UNKNOWN";
    }

    default String modelName() {
        return "UNKNOWN";
    }
}