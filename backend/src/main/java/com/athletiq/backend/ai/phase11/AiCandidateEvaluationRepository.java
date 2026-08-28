package com.athletiq.backend.ai.phase11;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiCandidateEvaluationRepository
        extends JpaRepository<AiCandidateEvaluationEntity, Long> {

    Optional<AiCandidateEvaluationEntity>
    findFirstByApplicationIdOrderByEvaluatedAtDesc(
            Long applicationId
    );

    long countByEventId(Long eventId);
        long deleteByApplicationId(Long applicationId);
    @Query("""
        select count(distinct e.applicationId)
        from AiCandidateEvaluationEntity e
        where e.eventId = :eventId
    """)
    long countDistinctApplicationIdsByEventId(
            @Param("eventId") Long eventId
    );
}