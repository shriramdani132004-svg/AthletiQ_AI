package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.event.requirements.repository.EventRequirementsRepository;
import com.athletiq.backend.objectiveevaluation.dto.EligibilityCheckResult;
import com.athletiq.backend.objectiveevaluation.dto.EligibilityResult;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedAnswer;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedApplicationAnswers;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ApplicationEligibilityService {

    private final EventRequirementsRepository
            eventRequirementsRepository;

    private final ApplicationAnswerNormalizationService
            normalizationService;

    public ApplicationEligibilityService(
            EventRequirementsRepository eventRequirementsRepository,
            ApplicationAnswerNormalizationService normalizationService
    ) {

        this.eventRequirementsRepository =
                eventRequirementsRepository;

        this.normalizationService =
                normalizationService;
    }

    public EligibilityResult evaluate(
            Application application
    ) {

        if(application == null) {

            throw new IllegalArgumentException(
                    "Application is required."
            );
        }

        if(
                application.getEvent() == null ||
                application.getEvent().getId() == null
        ) {

            throw new IllegalArgumentException(
                    "Application event is required."
            );
        }

        NormalizedApplicationAnswers normalized =
                normalizationService.normalize(
                        application
                );

        EventRequirements requirements =
                eventRequirementsRepository
                        .findByEventId(
                                application.getEvent().getId()
                        )
                        .orElse(null);

        /*
         * No requirements means there are currently no
         * mandatory eligibility restrictions.
         */
        if(requirements == null) {

            return new EligibilityResult(
                    true,
                    List.of(),
                    List.of(),
                    List.of(
                            "No event-specific eligibility requirements configured."
                    ),
                    "Candidate is eligible because no event-specific eligibility requirements are configured."
            );
        }

        List<EligibilityCheckResult> checks =
                new ArrayList<>();

        List<String> failed =
                new ArrayList<>();

        List<String> passed =
                new ArrayList<>();

        evaluateAge(
                requirements,
                normalized,
                checks,
                failed,
                passed
        );

        evaluatePosition(
                requirements,
                normalized,
                checks,
                failed,
                passed
        );

        evaluateMinimumExperience(
                requirements,
                normalized,
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Achievements",
                requirements.getRequiredAchievements(),
                answer(
                        normalized,
                        "achievements"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Skills",
                requirements.getRequiredSkills(),
                answer(
                        normalized,
                        "skills"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Performance",
                requirements.getPerformanceRequirements(),
                firstAnswer(
                        normalized,
                        "performance",
                        "performanceinformation",
                        "sportsinformation"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Fitness",
                requirements.getFitnessRequirements(),
                firstAnswer(
                        normalized,
                        "fitness",
                        "fitnessinformation"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Availability",
                requirements.getAvailabilityRequirements(),
                firstAnswer(
                        normalized,
                        "availability",
                        "availabilityinformation"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Eligibility Conditions",
                requirements.getEligibilityConditions(),
                firstAnswer(
                        normalized,
                        "eligibility",
                        "eligibilityconditions"
                ),
                checks,
                failed,
                passed
        );

        evaluateTextRequirement(
                "Event-specific Requirements",
                requirements.getEventSpecificRequirements(),
                firstAnswer(
                        normalized,
                        "eventrequirements",
                        "eventspecificrequirements"
                ),
                checks,
                failed,
                passed
        );

        boolean eligible =
                failed.isEmpty();

        String explanation =
                eligible
                        ? "Candidate is eligible. All configured mandatory eligibility checks passed."
                        : "Candidate is ineligible because one or more mandatory eligibility checks failed: " +
                                String.join(
                                        " | ",
                                        failed
                                );

        return new EligibilityResult(
                eligible,
                List.copyOf(checks),
                List.copyOf(failed),
                List.copyOf(passed),
                explanation
        );
    }

    private void evaluateAge(
            EventRequirements requirements,
            NormalizedApplicationAnswers normalized,
            List<EligibilityCheckResult> checks,
            List<String> failed,
            List<String> passed
    ) {

        Integer minAge =
                requirements.getMinAge();

        Integer maxAge =
                requirements.getMaxAge();

        if(
                minAge == null &&
                maxAge == null
        ) {

            return;
        }

        NormalizedAnswer answer =
                normalized.answers().get(
                        "age"
                );

        Integer candidateAge =
                toInteger(
                        answer == null
                                ? null
                                : answer.normalizedValue()
                );

        String requiredValue =
                ageRequirement(
                        minAge,
                        maxAge
                );

        if(candidateAge == null) {

            addFailure(
                    "Age",
                    true,
                    null,
                    requiredValue,
                    "Candidate age is required to evaluate the configured age range.",
                    checks,
                    failed
            );

            return;
        }

        boolean valid =
                (minAge == null ||
                        candidateAge >= minAge)
                &&
                (maxAge == null ||
                        candidateAge <= maxAge);

        if(valid) {

            addPass(
                    "Age",
                    true,
                    String.valueOf(
                            candidateAge
                    ),
                    requiredValue,
                    "Candidate age satisfies the configured age range.",
                    checks,
                    passed
            );

        } else {

            addFailure(
                    "Age",
                    true,
                    String.valueOf(
                            candidateAge
                    ),
                    requiredValue,
                    "Candidate age falls outside the configured age range.",
                    checks,
                    failed
            );
        }
    }

    private void evaluatePosition(
            EventRequirements requirements,
            NormalizedApplicationAnswers normalized,
            List<EligibilityCheckResult> checks,
            List<String> failed,
            List<String> passed
    ) {

        String configured =
                clean(
                        requirements.getRequiredPositions()
                );

        if(configured == null) {
            return;
        }

        NormalizedAnswer answer =
                firstAnswer(
                        normalized,
                        "position",
                        "positions",
                        "role"
                );

        String candidate =
                clean(
                        answer == null
                                ? null
                                : answer.normalizedValue()
                );

        List<String> allowed =
                splitValues(
                        configured
                );

        if(candidate == null) {

            addFailure(
                    "Position",
                    true,
                    null,
                    String.join(
                            ", ",
                            allowed
                    ),
                    "Candidate position is required.",
                    checks,
                    failed
            );

            return;
        }

        boolean valid =
                allowed.stream()
                        .anyMatch(
                                allowedPosition ->
                                        allowedPosition.equalsIgnoreCase(
                                                candidate
                                        )
                        );

        if(valid) {

            addPass(
                    "Position",
                    true,
                    candidate,
                    String.join(
                            ", ",
                            allowed
                    ),
                    "Candidate position matches a configured event position.",
                    checks,
                    passed
            );

        } else {

            addFailure(
                    "Position",
                    true,
                    candidate,
                    String.join(
                            ", ",
                            allowed
                    ),
                    "Candidate position does not match a configured event position.",
                    checks,
                    failed
            );
        }
    }

    private void evaluateMinimumExperience(
            EventRequirements requirements,
            NormalizedApplicationAnswers normalized,
            List<EligibilityCheckResult> checks,
            List<String> failed,
            List<String> passed
    ) {

        String configured =
                clean(
                        requirements.getMinimumExperience()
                );

        if(configured == null) {
            return;
        }

        NormalizedAnswer answer =
                firstAnswer(
                        normalized,
                        "experience",
                        "minimumexperience",
                        "experienceyears"
                );

        String candidate =
                clean(
                        answer == null
                                ? null
                                : answer.normalizedValue()
                );

        if(candidate == null) {

            addFailure(
                    "Experience",
                    true,
                    null,
                    configured,
                    "Candidate experience is required.",
                    checks,
                    failed
            );

            return;
        }

        /*
         * The existing requirements model stores minimum experience
         * as free text. We therefore only perform deterministic text
         * matching here. Numeric interpretation belongs to criterion
         * scoring where a formal numeric field mapping exists.
         */
        boolean valid =
                candidate
                        .toLowerCase(
                                Locale.ROOT
                        )
                        .contains(
                                configured.toLowerCase(
                                        Locale.ROOT
                                )
                        );

        if(valid) {

            addPass(
                    "Experience",
                    true,
                    candidate,
                    configured,
                    "Candidate experience contains the configured minimum-experience requirement.",
                    checks,
                    passed
            );

        } else {

            addFailure(
                    "Experience",
                    true,
                    candidate,
                    configured,
                    "Candidate experience does not satisfy the configured text requirement.",
                    checks,
                    failed
            );
        }
    }

    private void evaluateTextRequirement(
            String requirement,
            String configured,
            Object candidateValue,
            List<EligibilityCheckResult> checks,
            List<String> failed,
            List<String> passed
    ) {

        String required =
                clean(
                        configured
                );

        if(required == null) {
            return;
        }

        String candidate =
                clean(
                        candidateValue
                );

        if(candidate == null) {

            addFailure(
                    requirement,
                    true,
                    null,
                    required,
                    "Required candidate information is missing.",
                    checks,
                    failed
            );

            return;
        }

        List<String> requiredTokens =
                splitValues(
                        required
                );

        String candidateLower =
                candidate.toLowerCase(
                        Locale.ROOT
                );

        List<String> missing =
                requiredTokens.stream()
                        .map(
                                token ->
                                        token.toLowerCase(
                                                Locale.ROOT
                                        )
                        )
                        .filter(
                                token ->
                                        !candidateLower.contains(
                                                token
                                        )
                        )
                        .toList();

        if(missing.isEmpty()) {

            addPass(
                    requirement,
                    true,
                    candidate,
                    required,
                    "Candidate information satisfies the configured requirement.",
                    checks,
                    passed
            );

        } else {

            addFailure(
                    requirement,
                    true,
                    candidate,
                    required,
                    "Candidate information does not contain all required terms: " +
                            String.join(
                                    ", ",
                                    missing
                            ),
                    checks,
                    failed
            );
        }
    }

    private Object answer(
            NormalizedApplicationAnswers normalized,
            String fieldKey
    ) {

        NormalizedAnswer answer =
                normalized.answers().get(
                        fieldKey
                );

        return answer == null
                ? null
                : answer.normalizedValue();
    }

    private NormalizedAnswer firstAnswer(
            NormalizedApplicationAnswers normalized,
            String... fieldKeys
    ) {

        for(String fieldKey : fieldKeys) {

            NormalizedAnswer answer =
                    normalized.answers().get(
                            fieldKey
                    );

            if(
                    answer != null &&
                    answer.present()
            ) {

                return answer;
            }
        }

        return null;
    }

    private Integer toInteger(
            Object value
    ) {

        if(value == null) {
            return null;
        }

        if(value instanceof Number number) {
            return number.intValue();
        }

        try {

            return new BigDecimal(
                    String.valueOf(
                            value
                    )
            ).intValueExact();

        } catch(Exception exception) {

            return null;
        }
    }

    private List<String> splitValues(
            String value
    ) {

        String[] parts =
                value.split(
                        "[,;|]"
                );

        List<String> result =
                new ArrayList<>();

        for(String part : parts) {

            String normalized =
                    clean(
                            part
                    );

            if(normalized != null) {

                result.add(
                        normalized
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private String ageRequirement(
            Integer minAge,
            Integer maxAge
    ) {

        if(
                minAge != null &&
                maxAge != null
        ) {

            return minAge +
                    " - " +
                    maxAge;
        }

        if(minAge != null) {
            return ">= " + minAge;
        }

        return "<= " + maxAge;
    }

    private String clean(
            Object value
    ) {

        if(value == null) {
            return null;
        }

        String text =
                String.valueOf(
                        value
                ).trim();

        return text.isEmpty()
                ? null
                : text;
    }

    private void addPass(
            String requirement,
            boolean required,
            String candidateValue,
            String requiredValue,
            String explanation,
            List<EligibilityCheckResult> checks,
            List<String> passed
    ) {

        checks.add(
                new EligibilityCheckResult(
                        requirement,
                        required,
                        true,
                        candidateValue,
                        requiredValue,
                        explanation
                )
        );

        passed.add(
                requirement +
                        ": " +
                        explanation
        );
    }

    private void addFailure(
            String requirement,
            boolean required,
            String candidateValue,
            String requiredValue,
            String explanation,
            List<EligibilityCheckResult> checks,
            List<String> failed
    ) {

        checks.add(
                new EligibilityCheckResult(
                        requirement,
                        required,
                        false,
                        candidateValue,
                        requiredValue,
                        explanation
                )
        );

        failed.add(
                requirement +
                        ": " +
                        explanation
        );
    }
}