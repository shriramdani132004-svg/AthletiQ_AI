package com.athletiq.backend.objectiveevaluation.repository;

import com.athletiq.backend.objectiveevaluation.entity.CriterionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriterionEvaluationRepository
        extends JpaRepository<CriterionEvaluation, Long> {

    List<CriterionEvaluation>
    findByObjectiveEvaluationIdOrderByIdAsc(
            Long objectiveEvaluationId
    );

    void deleteByObjectiveEvaluationId(
            Long objectiveEvaluationId
    );
}