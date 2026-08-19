package com.athletiq.backend.ai.phase11;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;

@Service
public class AiEvaluationService {

    private final AiEvaluationProvider provider;
    private final JsonMapper objectMapper;

    public AiEvaluationService(
            AiEvaluationProvider provider,
            JsonMapper objectMapper
    ) {
        this.provider = provider;
        this.objectMapper = objectMapper;
    }

    public AiEvaluationResult evaluate(
            AiEvaluationProviderRequest request
    ) {

        AiEvaluationProviderResult providerResult =
                provider.evaluate(request);

        if(providerResult == null) {
            throw new IllegalStateException(
                    "AI provider returned no result."
            );
        }

        try {

            AiEvaluationResult result =
                    objectMapper.readValue(
                            providerResult.rawResponse(),
                            AiEvaluationResult.class
                    );

            validate(result);

            return result;

        } catch(Exception exception) {

            throw new IllegalStateException(
                    "AI evaluation result could not be parsed or validated.",
                    exception
            );
        }
    }

    private void validate(
            AiEvaluationResult result
    ) {

        if(result == null) {
            throw new IllegalArgumentException(
                    "AI evaluation result is required."
            );
        }

        if(
                result.candidateReference() == null ||
                result.candidateReference().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "AI candidate reference is required."
            );
        }

        if(result.score() == null) {
            throw new IllegalArgumentException(
                    "AI score is required."
            );
        }

        if(
                result.score() < 0 ||
                result.score() > 100
        ) {
            throw new IllegalArgumentException(
                    "AI score must be between 0 and 100."
            );
        }

        if(
                result.assessment() == null ||
                result.assessment().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "AI assessment is required."
            );
        }

        if(
                result.recommendation() == null ||
                result.recommendation().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "AI recommendation is required."
            );
        }

        if(
                result.explanation() == null ||
                result.explanation().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "AI explanation is required."
            );
        }
    }
}