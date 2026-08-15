package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.objectiveevaluation.dto.CriterionScoreExplanation;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtraction;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtractionResult;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedCriterionScore;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveScoreExplanation;
import com.athletiq.backend.objectiveevaluation.dto.WeightedCriterionScore;
import com.athletiq.backend.objectiveevaluation.dto.WeightedObjectiveScoreResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObjectiveScoreExplanationService {

    private final CriterionValueExtractionService
            extractionService;

    private final CriterionScoreNormalizationService
            normalizationService;

    private final ObjectiveScoreCalculationService
            scoreCalculationService;

    public ObjectiveScoreExplanationService(
            CriterionValueExtractionService extractionService,
            CriterionScoreNormalizationService normalizationService,
            ObjectiveScoreCalculationService scoreCalculationService
    ){

        this.extractionService =
                extractionService;

        this.normalizationService =
                normalizationService;

        this.scoreCalculationService =
                scoreCalculationService;
    }

    public ObjectiveScoreExplanation explain(
            Application application
    ){

        if(application == null){

            throw new IllegalArgumentException(
                    "Application is required."
            );
        }

        CriterionValueExtractionResult extracted =
                extractionService.extract(
                        application
                );

        var normalized =
                normalizationService.normalize(
                        application
                );

        WeightedObjectiveScoreResult calculated =
                scoreCalculationService.calculate(
                        application
                );

        Map<Long,CriterionValueExtraction>
                extractionById =
                new HashMap<>();

        for(
                CriterionValueExtraction criterion :
                extracted.criteria()
        ){

            if(criterion.criterionId() != null){

                extractionById.put(
                        criterion.criterionId(),
                        criterion
                );
            }
        }

        Map<Long,NormalizedCriterionScore>
                normalizationById =
                new HashMap<>();

        for(
                NormalizedCriterionScore criterion :
                normalized.criteria()
        ){

            if(criterion.criterionId() != null){

                normalizationById.put(
                        criterion.criterionId(),
                        criterion
                );
            }
        }

        List<CriterionScoreExplanation>
                explanations =
                new ArrayList<>();

        for(
                WeightedCriterionScore weighted :
                calculated.criteria()
        ){

            CriterionValueExtraction extraction =
                    extractionById.get(
                            weighted.criterionId()
                    );

            NormalizedCriterionScore normalizedCriterion =
                    normalizationById.get(
                            weighted.criterionId()
                    );

            String evidence =
                    buildEvidence(
                            extraction,
                            normalizedCriterion
                    );

            String explanation =
                    buildCriterionExplanation(
                            extraction,
                            normalizedCriterion,
                            weighted
                    );

            explanations.add(
                    new CriterionScoreExplanation(
                            weighted.criterionId(),
                            weighted.criterionName(),
                            weighted.criterionType() == null
                                    ? null
                                    : weighted.criterionType()
                                            .name(),
                            extraction == null
                                    ? null
                                    : extraction.sourceFieldKey(),
                            extraction == null
                                    ? null
                                    : extraction.originalValue(),
                            weighted.normalizedScore(),
                            weighted.weight(),
                            weighted.weightedContribution(),
                            evidence,
                            explanation
                    )
            );
        }

        String summary =
                buildSummary(
                        calculated,
                        explanations
                );

        return new ObjectiveScoreExplanation(
                calculated.applicationId(),
                calculated.eventId(),
                calculated.objectiveScore(),
                calculated.totalWeight(),
                List.copyOf(
                        explanations
                ),
                summary
        );
    }

    private String buildEvidence(
            CriterionValueExtraction extraction,
            NormalizedCriterionScore normalized
    ){

        if(extraction == null){

            return "No criterion extraction record available.";
        }

        if(!extraction.mapped()){

            return "No deterministic application field mapping was found.";
        }

        if(
                extraction.normalizedValue() == null
        ){

            return "Mapped application field is present but contains no usable value.";
        }

        String source =
                extraction.sourceFieldKey();

        String raw =
                extraction.originalValue();

        if(normalized == null ||
                !normalized.valid()){

            return
                    "Application field '" +
                            source +
                            "' supplied value '" +
                            String.valueOf(raw) +
                            "', but deterministic normalization could not produce a valid score.";

        }

        return
                "Field '" +
                        source +
                        "' supplied raw value '" +
                        String.valueOf(raw) +
                        "' and normalized to " +
                        normalized.normalizedScore()
                                .stripTrailingZeros()
                                .toPlainString() +
                        "/100.";
    }

    private String buildCriterionExplanation(
            CriterionValueExtraction extraction,
            NormalizedCriterionScore normalized,
            WeightedCriterionScore weighted
    ){

        if(!weighted.valid()){

            return weighted.explanation();
        }

        BigDecimal score =
                weighted.normalizedScore();

        BigDecimal weight =
                weighted.weight();

        BigDecimal contribution =
                weighted.weightedContribution();

        return
                weighted.criterionName() +
                        " produced a normalized score of " +
                        score.stripTrailingZeros()
                                .toPlainString() +
                        "/100. The criterion weight is " +
                        weight.stripTrailingZeros()
                                .toPlainString() +
                        "%, contributing " +
                        contribution.stripTrailingZeros()
                                .toPlainString() +
                        " points to the objective score.";
    }

    private String buildSummary(
            WeightedObjectiveScoreResult calculated,
            List<CriterionScoreExplanation> explanations
    ){

        if(!calculated.valid()){

            return
                    "Objective score could not be finalized because one or more criteria are invalid or incomplete.";
        }

        long validCriteria =
                explanations.stream()
                        .filter(
                                explanation ->
                                        explanation.weightedContribution()
                                                != null
                        )
                        .count();

        return
                "Objective score " +
                        calculated.objectiveScore()
                                .stripTrailingZeros()
                                .toPlainString() +
                        "/100 was calculated deterministically from " +
                        validCriteria +
                        " valid weighted criteria. " +
                        "The score is reproducible from the recorded criterion values, normalization rules, and weights.";
    }
}