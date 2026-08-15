package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.evaluation.repository.EvaluationCriterionRepository;
import com.athletiq.backend.objectiveevaluation.dto.CriterionScoreExplanation;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtraction;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtractionResult;
import com.athletiq.backend.objectiveevaluation.dto.EligibilityResult;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveEvaluationResponse;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveScoreExplanation;
import com.athletiq.backend.objectiveevaluation.dto.WeightedCriterionScore;
import com.athletiq.backend.objectiveevaluation.dto.WeightedObjectiveScoreResult;
import com.athletiq.backend.objectiveevaluation.entity.CriterionEvaluation;
import com.athletiq.backend.objectiveevaluation.entity.EligibilityStatus;
import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluation;
import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluationStatus;
import com.athletiq.backend.objectiveevaluation.repository.CriterionEvaluationRepository;
import com.athletiq.backend.objectiveevaluation.repository.ObjectiveEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObjectiveEvaluationService {

    private final ApplicationRepository applicationRepository;

    private final ObjectiveEvaluationRepository
            objectiveEvaluationRepository;

    private final CriterionEvaluationRepository
            criterionEvaluationRepository;

    private final EvaluationCriterionRepository
            evaluationCriterionRepository;

    private final ApplicationEligibilityService
            eligibilityService;

    private final CriterionValueExtractionService
            extractionService;

    private final ObjectiveScoreCalculationService
            scoreCalculationService;

    private final ObjectiveScoreExplanationService
            explanationService;

    private final JsonMapper jsonMapper;

    public ObjectiveEvaluationService(
            ApplicationRepository applicationRepository,
            ObjectiveEvaluationRepository objectiveEvaluationRepository,
            CriterionEvaluationRepository criterionEvaluationRepository,
            EvaluationCriterionRepository evaluationCriterionRepository,
            ApplicationEligibilityService eligibilityService,
            CriterionValueExtractionService extractionService,
            ObjectiveScoreCalculationService scoreCalculationService,
            ObjectiveScoreExplanationService explanationService,
            JsonMapper jsonMapper
    ) {

        this.applicationRepository =
                applicationRepository;

        this.objectiveEvaluationRepository =
                objectiveEvaluationRepository;

        this.criterionEvaluationRepository =
                criterionEvaluationRepository;

        this.evaluationCriterionRepository =
                evaluationCriterionRepository;

        this.eligibilityService =
                eligibilityService;

        this.extractionService =
                extractionService;

        this.scoreCalculationService =
                scoreCalculationService;

        this.explanationService =
                explanationService;

        this.jsonMapper =
                jsonMapper;
    }

    @Transactional
    public ObjectiveEvaluationResponse evaluate(
            Long organizerId,
            Long eventId,
            Long applicationId
    ) {

        Application application =
                resolveOwnedApplication(
                        organizerId,
                        eventId,
                        applicationId
                );

        ObjectiveEvaluation evaluation =
                objectiveEvaluationRepository
                        .findByApplicationIdAndEventId(
                                applicationId,
                                eventId
                        )
                        .orElseGet(
                                ObjectiveEvaluation::new
                        );

        prepareEvaluation(
                evaluation,
                application
        );

        try {

            EligibilityResult eligibility =
                    eligibilityService.evaluate(
                            application
                    );

            evaluation.setEligibilityStatus(
                    eligibility.eligible()
                            ? EligibilityStatus.ELIGIBLE
                            : EligibilityStatus.INELIGIBLE
            );

            evaluation.setEligibilityExplanation(
                    eligibility.explanation()
            );

            if(!eligibility.eligible()) {

                evaluation.setObjectiveScore(
                        null
                );

                evaluation.setScoreExplanation(
                        "Objective score was not calculated because the application failed one or more mandatory eligibility checks."
                );

                evaluation.setStatus(
                        ObjectiveEvaluationStatus.EVALUATED
                );

                evaluation.setCalculationVersion(
                        nextCalculationVersion(
                                evaluation
                        )
                );

                evaluation.setCalculatedAt(
                        LocalDateTime.now()
                );

                evaluation.setConfigurationSnapshot(
                        buildConfigurationSnapshot(
                                application,
                                eligibility,
                                List.of()
                        )
                );

                evaluation.setConfigurationHash(
                        sha256(
                                evaluation.getConfigurationSnapshot()
                        )
                );

                ObjectiveEvaluation saved =
                        objectiveEvaluationRepository.save(
                                evaluation
                        );

                replaceCriterionEvaluations(
                        saved,
                        List.of()
                );

                return toResponse(
                        saved,
                        List.of()
                );
            }

            CriterionValueExtractionResult extracted =
                    extractionService.extract(
                            application
                    );

            WeightedObjectiveScoreResult calculated =
                    scoreCalculationService.calculate(
                            application
                    );

            ObjectiveScoreExplanation explanation =
                    explanationService.explain(
                            application
                    );

            if(!calculated.valid()) {

                throw new IllegalStateException(
                        calculated.explanation()
                );
            }

            evaluation.setObjectiveScore(
                    calculated.objectiveScore()
            );

            evaluation.setScoreExplanation(
                    explanation.summary()
            );

            evaluation.setStatus(
                    ObjectiveEvaluationStatus.EVALUATED
            );

            evaluation.setCalculationVersion(
                    nextCalculationVersion(
                            evaluation
                    )
            );

            evaluation.setCalculatedAt(
                    LocalDateTime.now()
            );

            evaluation.setConfigurationSnapshot(
                    buildConfigurationSnapshot(
                            application,
                            eligibility,
                            extracted.criteria()
                    )
            );

            evaluation.setConfigurationHash(
                    sha256(
                            evaluation.getConfigurationSnapshot()
                    )
            );

            ObjectiveEvaluation saved =
                    objectiveEvaluationRepository.save(
                            evaluation
                    );

            replaceCriterionEvaluations(
                    saved,
                    extracted.criteria(),
                    calculated
            );

            return toResponse(
                    saved,
                    explanation.criteria()
            );

        } catch(RuntimeException exception) {

            evaluation.setStatus(
                    ObjectiveEvaluationStatus.FAILED
            );

            evaluation.setFailureReason(
                    safeMessage(
                            exception
                    )
            );

            evaluation.setCalculatedAt(
                    null
            );

            objectiveEvaluationRepository.save(
                    evaluation
            );

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public ObjectiveEvaluationResponse getEvaluation(
            Long organizerId,
            Long eventId,
            Long applicationId
    ) {

        Application application =
                resolveOwnedApplication(
                        organizerId,
                        eventId,
                        applicationId
                );

        ObjectiveEvaluation evaluation =
                objectiveEvaluationRepository
                        .findByApplicationIdAndEventId(
                                applicationId,
                                eventId
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Objective evaluation does not exist for this application."
                                        )
                        );

        List<CriterionScoreExplanation> criteria =
                buildPersistedCriterionExplanations(
                        evaluation
                );

        return toResponse(
                evaluation,
                criteria
        );
    }

    @Transactional
    public ObjectiveEvaluationResponse recalculate(
            Long organizerId,
            Long eventId,
            Long applicationId
    ) {

        return evaluate(
                organizerId,
                eventId,
                applicationId
        );
    }

    private void prepareEvaluation(
            ObjectiveEvaluation evaluation,
            Application application
    ) {

        evaluation.setApplication(
                application
        );

        evaluation.setEvent(
                application.getEvent()
        );

        evaluation.setFormVersion(
                application.getFormVersion()
        );

        evaluation.setStatus(
                ObjectiveEvaluationStatus.EVALUATING
        );

        evaluation.setEligibilityStatus(
                EligibilityStatus.PENDING
        );

        evaluation.setFailureReason(
                null
        );
    }

    private Application resolveOwnedApplication(
            Long organizerId,
            Long eventId,
            Long applicationId
    ) {

        if(organizerId == null) {

            throw new IllegalArgumentException(
                    "Organizer ID is required."
            );
        }

        if(eventId == null) {

            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        if(applicationId == null) {

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        Application application =
                applicationRepository
                        .findById(
                                applicationId
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Application not found."
                                        )
                        );

        if(
                application.getEvent() == null ||
                application.getEvent().getId() == null ||
                !eventId.equals(
                        application.getEvent().getId()
                )
        ) {

            throw new IllegalArgumentException(
                    "Application does not belong to the requested event."
            );
        }

        if(
                application.getEvent().getOrganizerId() == null ||
                !organizerId.equals(
                        application.getEvent().getOrganizerId()
                )
        ) {

            throw new IllegalArgumentException(
                    "Organizer does not own this event."
            );
        }

        return application;
    }

    private void replaceCriterionEvaluations(
            ObjectiveEvaluation evaluation,
            List<CriterionValueExtraction> extracted,
            WeightedObjectiveScoreResult calculated
    ) {

        criterionEvaluationRepository
                .deleteByObjectiveEvaluationId(
                        evaluation.getId()
                );

        Map<Long,WeightedCriterionScore> weightedById =
                new LinkedHashMap<>();

        for(
                WeightedCriterionScore weighted :
                calculated.criteria()
        ) {

            weightedById.put(
                    weighted.criterionId(),
                    weighted
            );
        }

        for(
                CriterionValueExtraction criterion :
                extracted
        ) {

            if(criterion.criterionId() == null) {
                continue;
            }

            EvaluationCriterion criterionEntity =
                    evaluationCriterionRepository
                            .findById(
                                    criterion.criterionId()
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Evaluation criterion " +
                                                            criterion.criterionId() +
                                                            " was not found."
                                            )
                            );

            WeightedCriterionScore weighted =
                    weightedById.get(
                            criterion.criterionId()
                    );

            CriterionEvaluation entity =
                    new CriterionEvaluation();

            entity.setObjectiveEvaluation(
                    evaluation
            );

            entity.setCriterion(
                    criterionEntity
            );

            entity.setCriterionNameSnapshot(
                    criterion.criterionName()
            );

            entity.setCriterionTypeSnapshot(
                    criterion.criterionType() == null
                            ? null
                            : criterion.criterionType().name()
            );

            entity.setWeightSnapshot(
                    criterion.weight()
            );

            entity.setMinScoreSnapshot(
                    criterion.minScore()
            );

            entity.setMaxScoreSnapshot(
                    criterion.maxScore()
            );

            entity.setRawValue(
                    criterion.originalValue()
            );

            entity.setEvidence(
                    criterion.explanation()
            );

            if(weighted != null) {

                entity.setNormalizedScore(
                        weighted.normalizedScore()
                );

                entity.setWeightedContribution(
                        weighted.weightedContribution()
                );

                entity.setExplanation(
                        weighted.explanation()
                );
            }

            criterionEvaluationRepository.save(
                    entity
            );
        }
    }

    private void replaceCriterionEvaluations(
            ObjectiveEvaluation evaluation,
            List<CriterionValueExtraction> extracted
    ) {

        criterionEvaluationRepository
                .deleteByObjectiveEvaluationId(
                        evaluation.getId()
                );
    }

    private Integer nextCalculationVersion(
            ObjectiveEvaluation evaluation
    ) {

        Integer current =
                evaluation.getCalculationVersion();

        if(current == null) {
            return 1;
        }

        return current + 1;
    }

    private String buildConfigurationSnapshot(
            Application application,
            EligibilityResult eligibility,
            List<CriterionValueExtraction> criteria
    ) {

        Map<String,Object> snapshot =
                new LinkedHashMap<>();

        snapshot.put(
                "eventId",
                application.getEvent().getId()
        );

        snapshot.put(
                "formVersionId",
                application.getFormVersion().getId()
        );

        snapshot.put(
                "eligibility",
                eligibility
        );

        snapshot.put(
                "criteria",
                criteria
        );

        try {

            return jsonMapper.writeValueAsString(
                    snapshot
            );

        } catch(Exception exception) {

            throw new IllegalStateException(
                    "Could not create evaluation configuration snapshot.",
                    exception
            );
        }
    }

    private String sha256(
            String value
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for(byte current : hash) {

                result.append(
                        String.format(
                                "%02x",
                                current
                        )
                );
            }

            return result.toString();

        } catch(NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 is unavailable.",
                    exception
            );
        }
    }

    private List<CriterionScoreExplanation>
    buildPersistedCriterionExplanations(
            ObjectiveEvaluation evaluation
    ) {

        List<CriterionEvaluation> persisted =
                criterionEvaluationRepository
                        .findByObjectiveEvaluationIdOrderByIdAsc(
                                evaluation.getId()
                        );

        List<CriterionScoreExplanation> result =
                new ArrayList<>();

        for(
                CriterionEvaluation criterion :
                persisted
        ) {

            result.add(
                    new CriterionScoreExplanation(
                            criterion.getCriterion() == null
                                    ? null
                                    : criterion.getCriterion().getId(),
                            criterion.getCriterionNameSnapshot(),
                            criterion.getCriterionTypeSnapshot(),
                            null,
                            criterion.getRawValue(),
                            criterion.getNormalizedScore(),
                            criterion.getWeightSnapshot(),
                            criterion.getWeightedContribution(),
                            criterion.getEvidence(),
                            criterion.getExplanation()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private ObjectiveEvaluationResponse toResponse(
            ObjectiveEvaluation evaluation,
            List<CriterionScoreExplanation> criteria
    ) {

        return new ObjectiveEvaluationResponse(
                evaluation.getId(),
                evaluation.getApplication().getId(),
                evaluation.getEvent().getId(),
                evaluation.getFormVersion().getId(),
                evaluation.getStatus(),
                evaluation.getEligibilityStatus(),
                evaluation.getObjectiveScore(),
                evaluation.getEligibilityExplanation(),
                evaluation.getScoreExplanation(),
                evaluation.getCalculationVersion(),
                evaluation.getCalculatedAt(),
                criteria
        );
    }

    private String safeMessage(
            RuntimeException exception
    ) {

        if(
                exception.getMessage() == null ||
                exception.getMessage().isBlank()
        ) {

            return exception.getClass()
                    .getSimpleName();
        }

        return exception.getMessage();
    }
}