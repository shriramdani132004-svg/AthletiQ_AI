package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.objectiveevaluation.dto.CriterionScoreNormalizationResult;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtraction;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtractionResult;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedCriterionScore;
import com.athletiq.backend.objectiveevaluation.dto.WeightedCriterionScore;
import com.athletiq.backend.objectiveevaluation.dto.WeightedObjectiveScoreResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectiveScoreCalculationService {

    private static final int SCALE = 4;

    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100);

    private final CriterionValueExtractionService
            extractionService;

    private final CriterionScoreNormalizationService
            normalizationService;

    public ObjectiveScoreCalculationService(
            CriterionValueExtractionService extractionService,
            CriterionScoreNormalizationService normalizationService
    ){

        this.extractionService =
                extractionService;

        this.normalizationService =
                normalizationService;
    }

    public WeightedObjectiveScoreResult calculate(
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

        CriterionScoreNormalizationResult normalized =
                normalizationService.normalize(
                        application
                );

        validateWeightConfiguration(
                extracted.criteria()
        );

        List<WeightedCriterionScore> weightedCriteria =
                new ArrayList<>();

        BigDecimal objectiveScore =
                BigDecimal.ZERO;

        for(
                CriterionValueExtraction criterion :
                extracted.criteria()
        ){

            NormalizedCriterionScore normalizedCriterion =
                    findNormalizedCriterion(
                            normalized.criteria(),
                            criterion.criterionId()
                    );

            if(
                    !criterion.mapped() ||
                    normalizedCriterion == null ||
                    !normalizedCriterion.valid()
            ){

                weightedCriteria.add(
                        new WeightedCriterionScore(
                                criterion.criterionId(),
                                criterion.criterionName(),
                                criterion.criterionType(),
                                normalizedCriterion == null
                                        ? null
                                        : normalizedCriterion.normalizedScore(),
                                criterion.weight(),
                                null,
                                false,
                                invalidExplanation(
                                        criterion,
                                        normalizedCriterion
                                )
                        )
                );

                continue;
            }

            BigDecimal contribution =
                    calculateContribution(
                            normalizedCriterion.normalizedScore(),
                            criterion.weight()
                    );

            objectiveScore =
                    objectiveScore.add(
                            contribution
                    );

            weightedCriteria.add(
                    new WeightedCriterionScore(
                            criterion.criterionId(),
                            criterion.criterionName(),
                            criterion.criterionType(),
                            normalizedCriterion.normalizedScore(),
                            criterion.weight(),
                            contribution,
                            true,
                            "Normalized score " +
                                    normalizedCriterion.normalizedScore()
                                            .stripTrailingZeros()
                                            .toPlainString() +
                                    " × weight " +
                                    criterion.weight()
                                            .stripTrailingZeros()
                                            .toPlainString() +
                                    "% = contribution " +
                                    contribution
                                            .stripTrailingZeros()
                                            .toPlainString() +
                                    "."
                    )
            );
        }

        BigDecimal finalScore =
                objectiveScore.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        boolean valid =
                weightedCriteria.stream()
                        .allMatch(
                                WeightedCriterionScore::valid
                        );

        if(!valid){

            return new WeightedObjectiveScoreResult(
                    extracted.applicationId(),
                    extracted.eventId(),
                    totalWeight(
                            extracted.criteria()
                    ),
                    null,
                    List.copyOf(
                            weightedCriteria
                    ),
                    false,
                    "Objective score could not be calculated because one or more enabled criteria have invalid or missing values."
            );
        }

        return new WeightedObjectiveScoreResult(
                extracted.applicationId(),
                extracted.eventId(),
                totalWeight(
                        extracted.criteria()
                ),
                finalScore,
                List.copyOf(
                        weightedCriteria
                ),
                true,
                buildExplanation(
                        weightedCriteria,
                        finalScore
                )
        );
    }

    private void validateWeightConfiguration(
            List<CriterionValueExtraction> criteria
    ){

        BigDecimal total =
                totalWeight(
                        criteria
                );

        if(
                total.compareTo(
                        ONE_HUNDRED
                ) != 0
        ){

            throw new IllegalStateException(
                    "Enabled evaluation criterion weights must total exactly 100%. Current total: " +
                            total.stripTrailingZeros()
                                    .toPlainString() +
                            "%."
            );
        }
    }

    private BigDecimal totalWeight(
            List<CriterionValueExtraction> criteria
    ){

        BigDecimal total =
                BigDecimal.ZERO;

        for(
                CriterionValueExtraction criterion :
                criteria
        ){

            if(
                    criterion.enabled() &&
                    criterion.weight() != null
            ){

                total =
                        total.add(
                                criterion.weight()
                        );
            }
        }

        return total.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal calculateContribution(
            BigDecimal normalizedScore,
            BigDecimal weight
    ){

        if(normalizedScore == null){
            return null;
        }

        if(weight == null){
            return null;
        }

        return normalizedScore
                .multiply(
                        weight
                )
                .divide(
                        ONE_HUNDRED,
                        SCALE,
                        RoundingMode.HALF_UP
                )
                .setScale(
                        SCALE,
                        RoundingMode.HALF_UP
                );
    }

    private NormalizedCriterionScore findNormalizedCriterion(
            List<NormalizedCriterionScore> criteria,
            Long criterionId
    ){

        for(
                NormalizedCriterionScore criterion :
                criteria
        ){

            if(
                    criterion.criterionId() != null &&
                    criterion.criterionId()
                            .equals(
                                    criterionId
                            )
            ){

                return criterion;
            }
        }

        return null;
    }

    private String invalidExplanation(
            CriterionValueExtraction criterion,
            NormalizedCriterionScore normalizedCriterion
    ){

        if(!criterion.mapped()){

            return
                    "Criterion has no deterministic application-field mapping.";
        }

        if(normalizedCriterion == null){

            return
                    "Normalized criterion result was not produced.";
        }

        return normalizedCriterion.explanation();
    }

    private String buildExplanation(
            List<WeightedCriterionScore> criteria,
            BigDecimal finalScore
    ){

        StringBuilder explanation =
                new StringBuilder();

        explanation.append(
                "Objective score = sum of all valid weighted criterion contributions. "
        );

        explanation.append(
                "Final score: "
        );

        explanation.append(
                finalScore
                        .stripTrailingZeros()
                        .toPlainString()
        );

        explanation.append(
                "/100."
        );

        return explanation.toString();
    }
}