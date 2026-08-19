package com.athletiq.backend.ai.phase11;

import java.time.Instant;

import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;

@Service
public class AiCandidateEvaluationPersistenceService {

    private final AiCandidateEvaluationRepository repository;
    private final JsonMapper jsonMapper;

    public AiCandidateEvaluationPersistenceService(
            AiCandidateEvaluationRepository repository,
            JsonMapper jsonMapper
    ) {
        this.repository = repository;
        this.jsonMapper = jsonMapper;
    }

    public AiCandidateEvaluationEntity save(
            Long eventId,
            Long applicationId,
            AiEvaluationResult result,
            String provider,
            String model,
            String evaluationVersion,
            String promptVersion,
            String auditVersion,
            String evaluationReference
    ) {

        if(eventId == null){
            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        if(applicationId == null){
            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if(result == null){
            throw new IllegalArgumentException(
                    "AI evaluation result is required."
            );
        }

        try {

            AiCandidateEvaluationEntity entity =
                    new AiCandidateEvaluationEntity(
                            result.candidateReference(),
                            eventId,
                            applicationId,
                            result.score(),
                            result.recommendation(),
                            result.assessment(),
                            jsonMapper.writeValueAsString(
                                    result.strengths()
                            ),
                            jsonMapper.writeValueAsString(
                                    result.weaknesses()
                            ),
                            result.experienceAnalysis(),
                            jsonMapper.writeValueAsString(
                                    result.requirementFit()
                            ),
                            result.positionSuitability(),
                            jsonMapper.writeValueAsString(
                                    result.concerns()
                            ),
                            result.explanation(),
                            provider,
                            model,
                            evaluationVersion,
                            promptVersion,
                            auditVersion,
                            evaluationReference,
                            Instant.now()
                    );

            return repository.save(entity);

        } catch(Exception exception){

            throw new IllegalStateException(
                    "Unable to persist AI evaluation.",
                    exception
            );
        }
    }

    public AiCandidateEvaluationEntity findLatestByApplication(
            Long applicationId
    ) {

        if(applicationId == null){
            return null;
        }

        return repository
                .findFirstByApplicationIdOrderByEvaluatedAtDesc(
                        applicationId
                )
                .orElse(null);
    }
        public long countByEventId(Long eventId) {
        if (eventId == null) {
            return 0;
        }

        return repository.countByEventId(eventId);
    }
}