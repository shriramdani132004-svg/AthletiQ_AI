package com.athletiq.backend.objectiveevaluation.entity;

import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "criterion_evaluations",
        indexes = {
                @Index(
                        name = "idx_criterion_evaluation_evaluation",
                        columnList = "objective_evaluation_id"
                ),
                @Index(
                        name = "idx_criterion_evaluation_criterion",
                        columnList = "criterion_id"
                )
        }
)
public class CriterionEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "objective_evaluation_id",
            nullable = false
    )
    private ObjectiveEvaluation objectiveEvaluation;

    /**
     * Reference to the live criterion configuration.
     *
     * The snapshot fields below preserve what was actually used
     * during calculation.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "criterion_id",
            nullable = false
    )
    private EvaluationCriterion criterion;

    @Column(
            name = "criterion_name_snapshot",
            nullable = false,
            length = 150
    )
    private String criterionNameSnapshot;

    @Column(
            name = "criterion_type_snapshot",
            nullable = false,
            length = 30
    )
    private String criterionTypeSnapshot;

    @Column(
            name = "weight_snapshot",
            nullable = false,
            precision = 7,
            scale = 4
    )
    private BigDecimal weightSnapshot;

    @Column(
            name = "min_score_snapshot",
            nullable = false,
            precision = 10,
            scale = 4
    )
    private BigDecimal minScoreSnapshot;

    @Column(
            name = "max_score_snapshot",
            nullable = false,
            precision = 10,
            scale = 4
    )
    private BigDecimal maxScoreSnapshot;

    /**
     * Original value extracted from submitted application data.
     */
    @Column(
            name = "raw_value",
            columnDefinition = "TEXT"
    )
    private String rawValue;

    /**
     * Deterministically normalized criterion score.
     */
    @Column(
            name = "normalized_score",
            precision = 10,
            scale = 4
    )
    private BigDecimal normalizedScore;

    /**
     * Weighted contribution to the final 0-100 objective score.
     */
    @Column(
            name = "weighted_contribution",
            precision = 10,
            scale = 4
    )
    private BigDecimal weightedContribution;

    @Column(
            name = "evidence",
            columnDefinition = "TEXT"
    )
    private String evidence;

    @Column(
            name = "explanation",
            columnDefinition = "TEXT"
    )
    private String explanation;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public CriterionEvaluation() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if(createdAt == null){
            createdAt = now;
        }

        if(updatedAt == null){
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ObjectiveEvaluation getObjectiveEvaluation() {
        return objectiveEvaluation;
    }

    public void setObjectiveEvaluation(
            ObjectiveEvaluation objectiveEvaluation
    ) {
        this.objectiveEvaluation =
                objectiveEvaluation;
    }

    public EvaluationCriterion getCriterion() {
        return criterion;
    }

    public void setCriterion(
            EvaluationCriterion criterion
    ) {
        this.criterion = criterion;
    }

    public String getCriterionNameSnapshot() {
        return criterionNameSnapshot;
    }

    public void setCriterionNameSnapshot(
            String criterionNameSnapshot
    ) {
        this.criterionNameSnapshot =
                criterionNameSnapshot;
    }

    public String getCriterionTypeSnapshot() {
        return criterionTypeSnapshot;
    }

    public void setCriterionTypeSnapshot(
            String criterionTypeSnapshot
    ) {
        this.criterionTypeSnapshot =
                criterionTypeSnapshot;
    }

    public BigDecimal getWeightSnapshot() {
        return weightSnapshot;
    }

    public void setWeightSnapshot(
            BigDecimal weightSnapshot
    ) {
        this.weightSnapshot =
                weightSnapshot;
    }

    public BigDecimal getMinScoreSnapshot() {
        return minScoreSnapshot;
    }

    public void setMinScoreSnapshot(
            BigDecimal minScoreSnapshot
    ) {
        this.minScoreSnapshot =
                minScoreSnapshot;
    }

    public BigDecimal getMaxScoreSnapshot() {
        return maxScoreSnapshot;
    }

    public void setMaxScoreSnapshot(
            BigDecimal maxScoreSnapshot
    ) {
        this.maxScoreSnapshot =
                maxScoreSnapshot;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(
            String rawValue
    ) {
        this.rawValue = rawValue;
    }

    public BigDecimal getNormalizedScore() {
        return normalizedScore;
    }

    public void setNormalizedScore(
            BigDecimal normalizedScore
    ) {
        this.normalizedScore =
                normalizedScore;
    }

    public BigDecimal getWeightedContribution() {
        return weightedContribution;
    }

    public void setWeightedContribution(
            BigDecimal weightedContribution
    ) {
        this.weightedContribution =
                weightedContribution;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(
            String evidence
    ) {
        this.evidence = evidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(
            String explanation
    ) {
        this.explanation = explanation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}