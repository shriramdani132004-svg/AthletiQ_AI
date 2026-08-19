package com.athletiq.backend.ai.phase11;

public class MockAiEvaluationProvider
        implements AiEvaluationProvider {

    @Override
    public AiEvaluationProviderResult evaluate(
            AiEvaluationProviderRequest request
    ) {

        if(request == null) {
            throw new IllegalArgumentException(
                    "AI provider request is required."
            );
        }

       String reference =
        request.candidateReference();

int hash =
        Math.abs(
                reference == null
                        ? 0
                        : reference.hashCode()
        );

int score =
        60 +
        (hash % 41);

String json =
        """
        {
          "candidateReference": "%s",
          "score": %d,
                  "assessment": "Strong candidate based on supplied evidence.",
                  "strengths": [
                    "Good requirement alignment.",
                    "Relevant positional evidence."
                  ],
                  "weaknesses": [
                    "Some experience evidence requires verification."
                  ],
                  "experienceAnalysis": "Relevant experience is present in the supplied application data.",
                  "requirementFit": [
                    {
                      "requirement": "Configured event requirements",
                      "status": "MATCH",
                      "evidence": "Mock provider evaluation."
                    }
                  ],
                  "positionSuitability": "Good positional fit based on supplied application evidence.",
                  "recommendation": "STRONG_FIT",
                  "concerns": [
                    "Some evidence should be verified by the organizer."
                  ],
                  "explanation": "Deterministic mock evaluation for local Phase 11 development."
                }
                """.formatted(
        reference,
        score
);

        return new AiEvaluationProviderResult(
                json,
                providerName(),
                modelName()
        );
    }

    @Override
    public String providerName() {
        return "MOCK";
    }

    @Override
    public String modelName() {
        return "athletiq-mock-v1";
    }
}