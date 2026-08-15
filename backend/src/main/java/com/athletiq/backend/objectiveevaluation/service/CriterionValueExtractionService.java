package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;
import com.athletiq.backend.event.evaluation.repository.EvaluationCriterionRepository;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtraction;
import com.athletiq.backend.objectiveevaluation.dto.CriterionValueExtractionResult;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedAnswer;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedApplicationAnswers;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class CriterionValueExtractionService {

    private final EvaluationCriterionRepository
            evaluationCriterionRepository;

    private final ApplicationAnswerNormalizationService
            normalizationService;

    private static final Map<String, Set<String>>
            CRITERION_ALIASES =
            createAliases();

    public CriterionValueExtractionService(
            EvaluationCriterionRepository evaluationCriterionRepository,
            ApplicationAnswerNormalizationService normalizationService
    ){

        this.evaluationCriterionRepository =
                evaluationCriterionRepository;

        this.normalizationService =
                normalizationService;
    }

    public CriterionValueExtractionResult extract(
            Application application
    ){

        if(application == null){

            throw new IllegalArgumentException(
                    "Application is required."
            );
        }

        if(application.getId() == null){

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if(
                application.getEvent() == null ||
                application.getEvent().getId() == null
        ){

            throw new IllegalArgumentException(
                    "Application event is required."
            );
        }

        NormalizedApplicationAnswers normalized =
                normalizationService.normalize(
                        application
                );

        Long eventId =
                application.getEvent().getId();

        List<EvaluationCriterion> criteria =
                evaluationCriterionRepository
                        .findByEventIdAndEnabledTrueOrderByDisplayOrderAsc(
                                eventId
                        );

        List<CriterionValueExtraction> results =
                new ArrayList<>();

        List<String> unmapped =
                new ArrayList<>();

        for(EvaluationCriterion criterion : criteria){

            String sourceFieldKey =
                    resolveSourceFieldKey(
                            criterion.getName(),
                            normalized.answers().keySet()
                    );

            if(sourceFieldKey == null){

                results.add(
                        new CriterionValueExtraction(
                                criterion.getId(),
                                criterion.getName(),
                                criterion.getCriterionType(),
                                criterion.getWeight(),
                                criterion.getMinScore(),
                                criterion.getMaxScore(),
                                criterion.isEnabled(),
                                false,
                                null,
                                null,
                                null,
                                "No application field could be deterministically mapped to criterion '" +
                                        criterion.getName() +
                                        "'."
                        )
                );

                unmapped.add(
                        criterion.getName()
                );

                continue;
            }

            NormalizedAnswer answer =
                    normalized.answers().get(
                            sourceFieldKey
                    );

            Object value =
                    answer == null
                            ? null
                            : answer.normalizedValue();

            String original =
                    answer == null
                            ? null
                            : answer.originalValue();

            Object typedValue =
                    coerceForCriterionType(
                            criterion.getCriterionType(),
                            value
                    );

            String explanation =
                    answer == null ||
                            !answer.present()
                            ? "Mapped field '" +
                                    sourceFieldKey +
                                    "' is missing from the application."
                            : "Criterion '" +
                                    criterion.getName() +
                                    "' mapped deterministically to field '" +
                                    sourceFieldKey +
                                    "'.";

            results.add(
                    new CriterionValueExtraction(
                            criterion.getId(),
                            criterion.getName(),
                            criterion.getCriterionType(),
                            criterion.getWeight(),
                            criterion.getMinScore(),
                            criterion.getMaxScore(),
                            criterion.isEnabled(),
                            true,
                            sourceFieldKey,
                            original,
                            typedValue,
                            explanation
                    )
            );
        }

        return new CriterionValueExtractionResult(
                application.getId(),
                eventId,
                List.copyOf(
                        results
                ),
                List.copyOf(
                        unmapped
                )
        );
    }

    private String resolveSourceFieldKey(
            String criterionName,
            Set<String> fieldKeys
    ){

        if(
                criterionName == null ||
                criterionName.isBlank()
        ){

            return null;
        }

        String canonicalCriterion =
                canonicalize(
                        criterionName
                );

        /*
         * First priority: exact canonical match.
         *
         * Example:
         * "Batting" -> "batting"
         * "recent_form" -> "recentform"
         */
        for(String fieldKey : fieldKeys){

            if(
                    canonicalize(
                            fieldKey
                    ).equals(
                            canonicalCriterion
                    )
            ){

                return fieldKey;
            }
        }

        /*
         * Second priority: explicitly defined aliases.
         */
        Set<String> aliases =
                CRITERION_ALIASES.get(
                        canonicalCriterion
                );

        if(aliases == null){
            return null;
        }

        for(String fieldKey : fieldKeys){

            String canonicalField =
                    canonicalize(
                            fieldKey
                    );

            if(aliases.contains(
                    canonicalField
            )){

                return fieldKey;
            }
        }

        return null;
    }

    private Object coerceForCriterionType(
            EvaluationCriterionType type,
            Object value
    ){

        if(value == null){
            return null;
        }

        if(type == null){
            return value;
        }

        return switch(type){

            case NUMERIC,
                 RATING ->
                    toBigDecimal(
                            value
                    );

            case BOOLEAN ->
                    toBoolean(
                            value
                    );

            case TEXT_ASSESSMENT ->
                    String.valueOf(
                            value
                    ).trim();
        };
    }

    private BigDecimal toBigDecimal(
            Object value
    ){

        if(value instanceof BigDecimal decimal){
            return decimal;
        }

        if(value instanceof Number number){

            return new BigDecimal(
                    number.toString()
            );
        }

        try{

            return new BigDecimal(
                    String.valueOf(
                            value
                    ).trim()
            );

        }catch(Exception exception){

            throw new IllegalArgumentException(
                    "Criterion value is not numeric: " +
                            value
            );
        }
    }

    private Boolean toBoolean(
            Object value
    ){

        if(value instanceof Boolean booleanValue){
            return booleanValue;
        }

        String normalized =
                String.valueOf(
                        value
                )
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );

        if(
                "true".equals(normalized) ||
                "yes".equals(normalized) ||
                "1".equals(normalized)
        ){

            return true;
        }

        if(
                "false".equals(normalized) ||
                "no".equals(normalized) ||
                "0".equals(normalized)
        ){

            return false;
        }

        throw new IllegalArgumentException(
                "Criterion value is not boolean: " +
                        value
        );
    }

    private String canonicalize(
            String value
    ){

        if(value == null){
            return "";
        }

        return value
                .trim()
                .toLowerCase(
                        Locale.ROOT
                )
                .replaceAll(
                        "[^a-z0-9]",
                        ""
                );
    }

    private static Map<String,Set<String>>
    createAliases(){

        Map<String,Set<String>> aliases =
                new HashMap<>();

        aliases.put(
                "recentform",
                Set.of(
                        "recentform",
                        "form",
                        "currentform",
                        "recentperformance"
                )
        );

        aliases.put(
                "fitness",
                Set.of(
                        "fitness",
                        "fitnessscore",
                        "fitnessrating",
                        "physicalfitness"
                )
        );

        aliases.put(
                "experience",
                Set.of(
                        "experience",
                        "years",
                        "yearsofexperience",
                        "experienceyears"
                )
        );

        aliases.put(
                "batting",
                Set.of(
                        "batting",
                        "batsmanship",
                        "battingperformance"
                )
        );

        aliases.put(
                "bowling",
                Set.of(
                        "bowling",
                        "bowlingperformance"
                )
        );

        aliases.put(
                "fielding",
                Set.of(
                        "fielding",
                        "fieldingperformance"
                )
        );

        aliases.put(
                "position",
                Set.of(
                        "position",
                        "playingposition",
                        "role"
                )
        );

        aliases.put(
                "age",
                Set.of(
                        "age",
                        "playerage"
                )
        );

        return Map.copyOf(
                aliases
        );
    }
}