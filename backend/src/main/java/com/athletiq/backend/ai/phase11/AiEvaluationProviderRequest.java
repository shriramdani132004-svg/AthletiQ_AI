package com.athletiq.backend.ai.phase11;

public record AiEvaluationProviderRequest(
        String candidateReference,
        String candidateContext,
        String requirementsContext,
        String applicationContext,
        String criteriaContext,
        String objectiveContext,
        String promptVersion
) {}