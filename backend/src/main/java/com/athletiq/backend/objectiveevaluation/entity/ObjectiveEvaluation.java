package com.athletiq.backend.objectiveevaluation.entity;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.form.entity.FormVersion;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "objective_evaluations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_objective_evaluation_application",
                        columnNames = "application_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_objective_evaluation_event",
                        columnList = "event_id"
                ),
                @Index(
                        name = "idx_objective_evaluation_application",
                        columnList = "application_id"
                ),
                @Index(
                        name = "idx_objective_evaluation_form_version",
                        columnList = "form_version_id"
                ),
                @Index(
                        name = "idx_objective_evaluation_status",
                        columnList = "status"
                )
        }
)
public class ObjectiveEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "application_id",
            nullable = false
    )
    private Application application;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "form_version_id",
            nullable = false
    )
    private FormVersion formVersion;

    /**
     * Configuration snapshot reference.
     *
     * At evaluation time this will contain a serialized snapshot
     * of the requirements/criteria configuration that produced the
     * result. This prevents future configuration edits from making
     * an old evaluation impossible to explain.
     */
    @Column(
            name = "configuration_snapshot",
            columnDefinition = "TEXT"
    )
    private String configurationSnapshot;

    /**
     * Optional deterministic configuration fingerprint.
     *
     * Future evaluation service logic can populate this with a hash
     * of the normalized evaluation configuration.
     */
    @Column(
            name = "configuration_hash",
            length = 128
    )
    private String configurationHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private ObjectiveEvaluationStatus status =
            ObjectiveEvaluationStatus.NOT_EVALUATED;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "eligibility_status",
            nullable = false,
            length = 30
    )
    private EligibilityStatus eligibilityStatus =
            EligibilityStatus.PENDING;

    @Column(
            name = "objective_score",
            precision = 7,
            scale = 4
    )
    private BigDecimal objectiveScore;

    @Column(
            name = "eligibility_explanation",
            columnDefinition = "TEXT"
    )
    private String eligibilityExplanation;

    @Column(
            name = "score_explanation",
            columnDefinition = "TEXT"
    )
    private String scoreExplanation;

    @Column(
            name = "failure_reason",
            columnDefinition = "TEXT"
    )
    private String failureReason;

    /**
     * Increments whenever an objective evaluation is recalculated.
     * Starts at 0 before the first successful calculation.
     */
    @Column(
            name = "calculation_version",
            nullable = false
    )
    private Integer calculationVersion = 0;

    @Column(
            name = "calculated_at"
    )
    private LocalDateTime calculatedAt;

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

    public ObjectiveEvaluation() {
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

        if(status == null){
            status =
                    ObjectiveEvaluationStatus.NOT_EVALUATED;
        }

        if(eligibilityStatus == null){
            eligibilityStatus =
                    EligibilityStatus.PENDING;
        }

        if(calculationVersion == null){
            calculationVersion = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(
            Application application
    ) {
        this.application = application;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(
            Event event
    ) {
        this.event = event;
    }

    public FormVersion getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(
            FormVersion formVersion
    ) {
        this.formVersion = formVersion;
    }

    public String getConfigurationSnapshot() {
        return configurationSnapshot;
    }

    public void setConfigurationSnapshot(
            String configurationSnapshot
    ) {
        this.configurationSnapshot =
                configurationSnapshot;
    }

    public String getConfigurationHash() {
        return configurationHash;
    }

    public void setConfigurationHash(
            String configurationHash
    ) {
        this.configurationHash =
                configurationHash;
    }

    public ObjectiveEvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(
            ObjectiveEvaluationStatus status
    ) {
        this.status = status;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(
            EligibilityStatus eligibilityStatus
    ) {
        this.eligibilityStatus =
                eligibilityStatus;
    }

    public BigDecimal getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(
            BigDecimal objectiveScore
    ) {
        this.objectiveScore = objectiveScore;
    }

    public String getEligibilityExplanation() {
        return eligibilityExplanation;
    }

    public void setEligibilityExplanation(
            String eligibilityExplanation
    ) {
        this.eligibilityExplanation =
                eligibilityExplanation;
    }

    public String getScoreExplanation() {
        return scoreExplanation;
    }

    public void setScoreExplanation(
            String scoreExplanation
    ) {
        this.scoreExplanation =
                scoreExplanation;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(
            String failureReason
    ) {
        this.failureReason = failureReason;
    }

    public Integer getCalculationVersion() {
        return calculationVersion;
    }

    public void setCalculationVersion(
            Integer calculationVersion
    ) {
        this.calculationVersion =
                calculationVersion;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(
            LocalDateTime calculatedAt
    ) {
        this.calculatedAt = calculatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}