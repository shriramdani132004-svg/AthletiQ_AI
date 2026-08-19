package com.athletiq.backend.ai.phase11;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AiCandidateEvaluationRepository
        extends JpaRepository<AiCandidateEvaluationEntity, Long> {

    Optional<AiCandidateEvaluationEntity>
    findFirstByApplicationIdOrderByEvaluatedAtDesc(
            Long applicationId
    );

    long countByEventId(Long eventId);
}