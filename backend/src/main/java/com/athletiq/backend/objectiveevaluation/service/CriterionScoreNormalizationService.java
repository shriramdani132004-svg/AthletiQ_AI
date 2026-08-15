package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;
import com.athletiq.backend.objectiveevaluation.dto.CriterionScoreNormalizationResult;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtraction;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtractionResult;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedCriterionScore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CriterionScoreNormalizationService {

    private static final int SCALE = 4;

    private static final BigDecimal ZERO =
            BigDecimal.ZERO.setScale(
                    SCALE,
                    RoundingMode.HALF_UP
            );

    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100)
                    .setScale(
                            SCALE,
                            RoundingMode.HALF_UP
                    );

    private final CriterionValueExtractionService
            extractionService;

    public CriterionScoreNormalizationService(
            CriterionValueExtractionService extractionService
    ){

        this.extractionService =
                extractionService;
    }

    public CriterionScoreNormalizationResult normalize(
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

        List<NormalizedCriterionScore> results =
                new ArrayList<>();

        BigDecimal total =
                BigDecimal.ZERO;

        int validCount = 0;

        for(
                CriterionValueExtraction criterion :
                extracted.criteria()
        ){

            NormalizedCriterionScore normalized =
                    normalizeCriterion(
                            criterion
                    );

            results.add(
                    normalized
            );

            if(
                    normalized.valid() &&
                    normalized.normalizedScore() != null
            ){

                total =
                        total.add(
                                normalized.normalizedScore()
                        );

                validCount++;
            }
        }

        BigDecimal average =
                validCount == 0
                        ? ZERO
                        : total.divide(
                                BigDecimal.valueOf(
                                        validCount
                                ),
                                SCALE,
                                RoundingMode.HALF_UP
                        );

        return new CriterionScoreNormalizationResult(
                extracted.applicationId(),
                extracted.eventId(),
                List.copyOf(
                        results
                ),
                average
        );
    }

    private NormalizedCriterionScore normalizeCriterion(
            CriterionValueExtraction criterion
    ){

        if(
                !criterion.enabled()
        ){

            return invalid(
                    criterion,
                    null,
                    "Criterion is disabled and cannot be scored."
            );
        }

        if(
                !criterion.mapped()
        ){

            return invalid(
                    criterion,
                    null,
                    "Criterion has no deterministic application-field mapping."
            );
        }

        BigDecimal min =
                criterion.minScore();

        BigDecimal max =
                criterion.maxScore();

        if(min == null || max == null){

            return invalid(
                    criterion,
                    null,
                    "Criterion minScore and maxScore must both be configured."
            );
        }

        if(
                max.compareTo(min) <= 0
        ){

            return invalid(
                    criterion,
                    null,
                    "Criterion maxScore must be greater than minScore."
            );
        }

        Object raw =
                criterion.normalizedValue();

        if(raw == null){

            return invalid(
                    criterion,
                    null,
                    "Criterion input value is missing."
            );
        }

        try{

            BigDecimal rawNumeric =
                    numericValue(
                            criterion.criterionType(),
                            raw
                    );

            BigDecimal normalized =
                    normalize(
                            rawNumeric,
                            min,
                            max
                    );

            return new NormalizedCriterionScore(
                    criterion.criterionId(),
                    criterion.criterionName(),
                    criterion.criterionType(),
                    rawNumeric,
                    min,
                    max,
                    normalized,
                    true,
                    buildExplanation(
                            rawNumeric,
                            min,
                            max,
                            normalized
                    )
            );

        }catch(IllegalArgumentException exception){

            return invalid(
                    criterion,
                    null,
                    exception.getMessage()
            );
        }
    }

    private BigDecimal normalize(
            BigDecimal raw,
            BigDecimal min,
            BigDecimal max
    ){

        /*
         * Linear normalization:
         *
         * ((raw - min) / (max - min)) * 100
         */

        BigDecimal clamped =
                clamp(
                        raw,
                        min,
                        max
                );

        BigDecimal range =
                max.subtract(
                        min
                );

        BigDecimal normalized =
                clamped
                        .subtract(min)
                        .divide(
                                range,
                                8,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                BigDecimal.valueOf(100)
                        );

        return clamp(
                normalized,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100)
        ).setScale(
                SCALE,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal clamp(
            BigDecimal value,
            BigDecimal minimum,
            BigDecimal maximum
    ){

        if(
                value.compareTo(minimum) < 0
        ){

            return minimum;
        }

        if(
                value.compareTo(maximum) > 0
        ){

            return maximum;
        }

        return value;
    }

    private BigDecimal numericValue(
            EvaluationCriterionType type,
            Object value
    ){

        if(type == null){

            throw new IllegalArgumentException(
                    "Criterion type is required."
            );
        }

        return switch(type){

            case NUMERIC,
                 RATING ->

                    toBigDecimal(
                            value
                    );

            case BOOLEAN ->

                    booleanToScore(
                            value
                    );

            case TEXT_ASSESSMENT ->

                    throw new IllegalArgumentException(
                            "TEXT_ASSESSMENT criteria require a deterministic numeric mapping before scoring."
                    );
        };
    }

    private BigDecimal toBigDecimal(
            Object value
    ){

        if(
                value instanceof BigDecimal decimal
        ){

            return decimal;
        }

        if(
                value instanceof Number number
        ){

            return new BigDecimal(
                    number.toString()
            );
        }

        String text =
                String.valueOf(
                        value
                ).trim();

        if(text.isEmpty()){

            throw new IllegalArgumentException(
                    "Criterion numeric value is empty."
            );
        }

        try{

            return new BigDecimal(
                    text
            );

        }catch(NumberFormatException exception){

            throw new IllegalArgumentException(
                    "Criterion value is not numeric: " +
                            text
            );
        }
    }

    private BigDecimal booleanToScore(
            Object value
    ){

        if(
                value instanceof Boolean booleanValue
        ){

            return booleanValue
                    ? BigDecimal.ONE
                    : BigDecimal.ZERO;
        }

        String normalized =
                String.valueOf(
                        value
                )
                .trim()
                .toLowerCase();

        if(
                "true".equals(normalized) ||
                "yes".equals(normalized) ||
                "1".equals(normalized)
        ){

            return BigDecimal.ONE;
        }

        if(
                "false".equals(normalized) ||
                "no".equals(normalized) ||
                "0".equals(normalized)
        ){

            return BigDecimal.ZERO;
        }

        throw new IllegalArgumentException(
                "Criterion boolean value is invalid: " +
                        value
        );
    }

    private NormalizedCriterionScore invalid(
            CriterionValueExtraction criterion,
            BigDecimal rawValue,
            String explanation
    ){

        return new NormalizedCriterionScore(
                criterion.criterionId(),
                criterion.criterionName(),
                criterion.criterionType(),
                rawValue,
                criterion.minScore(),
                criterion.maxScore(),
                null,
                false,
                explanation
        );
    }

    private String buildExplanation(
            BigDecimal raw,
            BigDecimal min,
            BigDecimal max,
            BigDecimal normalized
    ){

        return
                "Raw value " +
                        raw.stripTrailingZeros()
                                .toPlainString() +
                        " normalized from range [" +
                        min.stripTrailingZeros()
                                .toPlainString() +
                        ", " +
                        max.stripTrailingZeros()
                                .toPlainString() +
                        "] to 0-100, producing " +
                        normalized.stripTrailingZeros()
                                .toPlainString() +
                        ".";
    }
}