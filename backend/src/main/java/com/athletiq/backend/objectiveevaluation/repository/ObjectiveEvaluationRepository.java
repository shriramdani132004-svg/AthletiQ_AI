package com.athletiq.backend.objectiveevaluation.repository;

import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObjectiveEvaluationRepository
        extends JpaRepository<ObjectiveEvaluation, Long> {

    Optional<ObjectiveEvaluation>
    findByApplicationId(
            Long applicationId
    );

    boolean existsByApplicationId(
            Long applicationId
    );

    Optional<ObjectiveEvaluation>
    findByApplicationIdAndEventId(
            Long applicationId,
            Long eventId
    );}