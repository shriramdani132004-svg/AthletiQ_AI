package com.athletiq.backend.event.evaluation.repository;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationCriterionRepository
        extends JpaRepository<EvaluationCriterion, Long> {

    List<EvaluationCriterion> findByEventIdOrderByDisplayOrderAsc(Long eventId);

    boolean existsByEventIdAndNameIgnoreCase(
            Long eventId,
            String name
    );

    List<EvaluationCriterion> findByEventId(
            Long eventId
    );

    List<EvaluationCriterion>
    findByEventIdAndEnabledTrueOrderByDisplayOrderAsc(
            Long eventId
    );
}