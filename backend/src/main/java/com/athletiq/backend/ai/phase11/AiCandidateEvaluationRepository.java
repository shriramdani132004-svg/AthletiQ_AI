package com.athletiq.backend.ai.phase11;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiCandidateEvaluationRepository
        extends JpaRepository<AiCandidateEvaluationEntity, Long> {

    Optional<AiCandidateEvaluationEntity>
    findFirstByApplicationIdOrderByEvaluatedAtDesc(
            Long applicationId
    );
}