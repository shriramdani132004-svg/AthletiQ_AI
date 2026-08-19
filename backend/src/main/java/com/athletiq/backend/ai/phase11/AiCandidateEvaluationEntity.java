package com.athletiq.backend.ai.phase11;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_candidate_evaluations")
public class AiCandidateEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_reference", nullable = false)
    private String candidateReference;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "ai_score", nullable = false)
    private Double aiScore;

    @Column(name = "recommendation", nullable = false)
    private String recommendation;

    @Lob
    @Column(name = "assessment", nullable = false)
    private String assessment;

    @Lob
    @Column(name = "strengths_json", nullable = false)
    private String strengthsJson;

    @Lob
    @Column(name = "weaknesses_json", nullable = false)
    private String weaknessesJson;

    @Lob
    @Column(name = "experience_analysis", nullable = false)
    private String experienceAnalysis;

    @Lob
    @Column(name = "requirement_fit_json", nullable = false)
    private String requirementFitJson;

    @Lob
    @Column(name = "position_suitability", nullable = false)
    private String positionSuitability;

    @Lob
    @Column(name = "concerns_json", nullable = false)
    private String concernsJson;

    @Lob
    @Column(name = "explanation", nullable = false)
    private String explanation;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "evaluation_version", nullable = false)
    private String evaluationVersion;

    @Column(name = "prompt_version", nullable = false)
    private String promptVersion;

    @Column(name = "audit_version", nullable = false)
    private String auditVersion;

    @Column(name = "evaluation_reference", nullable = false)
    private String evaluationReference;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;

    protected AiCandidateEvaluationEntity() {
    }

    public AiCandidateEvaluationEntity(
        String candidateReference,
        Long eventId,
        Long applicationId,
        Double aiScore,
        String recommendation,
        String assessment,
        String strengthsJson,
        String weaknessesJson,
        String experienceAnalysis,
        String requirementFitJson,
        String positionSuitability,
        String concernsJson,
        String explanation,
        String provider,
        String model,
        String evaluationVersion,
        String promptVersion,
        String auditVersion,
        String evaluationReference,
        Instant evaluatedAt
) {
    this.candidateReference = candidateReference;
    this.eventId = eventId;
    this.applicationId = applicationId;
    this.aiScore = aiScore;
    this.recommendation = recommendation;
    this.assessment = assessment;
    this.strengthsJson = strengthsJson;
    this.weaknessesJson = weaknessesJson;
    this.experienceAnalysis = experienceAnalysis;
    this.requirementFitJson = requirementFitJson;
    this.positionSuitability = positionSuitability;
    this.concernsJson = concernsJson;
    this.explanation = explanation;
    this.provider = provider;
    this.model = model;
    this.evaluationVersion = evaluationVersion;
    this.promptVersion = promptVersion;
    this.auditVersion = auditVersion;
    this.evaluationReference = evaluationReference;
    this.evaluatedAt = evaluatedAt;
}

    public Double getAiScore() {
        return aiScore;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getRecommendation() {
        return recommendation;
    }
}