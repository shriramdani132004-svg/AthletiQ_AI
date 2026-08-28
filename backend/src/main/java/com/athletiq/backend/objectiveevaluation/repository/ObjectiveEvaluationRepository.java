package com.athletiq.backend.objectiveevaluation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluation;

public interface ObjectiveEvaluationRepository
        extends JpaRepository<ObjectiveEvaluation, Long> {

    Optional<ObjectiveEvaluation>
    findByApplicationId(
            Long applicationId
    );

    boolean existsByApplicationId(
            Long applicationId
    );
        long deleteByApplicationId(Long applicationId);
    Optional<ObjectiveEvaluation>
    findByApplicationIdAndEventId(
            Long applicationId,
            Long eventId
    );}