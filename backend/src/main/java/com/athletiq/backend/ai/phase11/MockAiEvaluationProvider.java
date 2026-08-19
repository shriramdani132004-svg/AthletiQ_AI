package com.athletiq.backend.ai.phase11;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockAiEvaluationProvider
        implements AiEvaluationProvider {

    private static final Pattern OBJECTIVE_SCORE_PATTERN =
            Pattern.compile(
                    "(?i)objective\\s+score\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)"
            );

    @Override
    public AiEvaluationProviderResult evaluate(
            AiEvaluationProviderRequest request
    ) {

        if(request == null){
            throw new IllegalArgumentException(
                    "AI provider request is required."
            );
        }

        String reference =
                safe(request.candidateReference());

        String applicationContext =
                safe(request.applicationContext());

        String requirementsContext =
                safe(request.requirementsContext());

        String criteriaContext =
                safe(request.criteriaContext());

        String objectiveContext =
                safe(request.objectiveContext());

        String evidence =
                String.join(
                        "\n",
                        applicationContext,
                        requirementsContext,
                        criteriaContext,
                        objectiveContext
                ).toLowerCase(
                        Locale.ROOT
                );

        int score =
                calculateScore(
                        evidence,
                        objectiveContext,
                        reference
                );

        String recommendation =
                recommendationFor(score);

        String assessment =
                assessmentFor(score);

        String positionSuitability =
                positionSuitabilityFor(score);

        String requirementStatus =
                score >= 70
                        ? "MATCH"
                        : score >= 50
                                ? "PARTIAL"
                                : "MISMATCH";

        String json =
                """
                {
                  "candidateReference": "%s",
                  "score": %d,
                  "assessment": "%s",
                  "strengths": [
                    "%s",
                    "%s"
                  ],
                  "weaknesses": [
                    "%s",
                    "%s"
                  ],
                  "experienceAnalysis": "%s",
                  "requirementFit": [
                    {
                      "requirement": "Configured event requirements",
                      "status": "%s",
                      "evidence": "Derived from submitted candidate evidence."
                    }
                  ],
                  "positionSuitability": "%s",
                  "recommendation": "%s",
                  "concerns": [
                    "%s"
                  ],
                  "explanation": "Deterministic evidence-calibrated evaluation for local deployment."
                }
                """.formatted(
                        jsonEscape(reference),
                        score,
                        jsonEscape(assessment),
                        jsonEscape(strengthFor(score)),
                        jsonEscape(secondStrengthFor(evidence)),
                        jsonEscape(weaknessFor(score)),
                        jsonEscape(secondWeaknessFor(evidence)),
                        jsonEscape(experienceAnalysisFor(evidence)),
                        requirementStatus,
                        jsonEscape(positionSuitability),
                        recommendation,
                        jsonEscape(concernFor(score))
                );

        return new AiEvaluationProviderResult(
                json,
                providerName(),
                modelName()
        );
    }

    private int calculateScore(
            String evidence,
            String objectiveContext,
            String reference
    ){

        int score = 25;

        if(!evidence.isBlank()){
            score += 10;
        }

        if(evidence.length() >= 250){
            score += 10;
        }

        if(evidence.length() >= 700){
            score += 10;
        }

        Double objectiveScore =
                extractObjectiveScore(
                        objectiveContext
                );

        if(objectiveScore != null){
            score += (int) Math.round(
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    objectiveScore
                            )
                    ) * 0.35
            );
        }

        String[] positiveSignals = {
                "experience",
                "years",
                "achievement",
                "tournament",
                "skill",
                "position",
                "performance",
                "available",
                "training",
                "goal"
        };

        for(String signal : positiveSignals){
            if(evidence.contains(signal)){
                score += 2;
            }
        }

        String[] negativeSignals = {
                "no experience",
                "none",
                "unavailable",
                "cannot",
                "unable",
                "injured",
                "invalid",
                "not applicable",
                "weak"
        };

        for(String signal : negativeSignals){
            if(evidence.contains(signal)){
                score -= 6;
            }
        }

        score +=
                Math.floorMod(
                        reference.hashCode(),
                        5
                );

        return clamp(
                score,
                0,
                100
        );
    }

    private Double extractObjectiveScore(
            String objectiveContext
    ){

        Matcher matcher =
                OBJECTIVE_SCORE_PATTERN.matcher(
                        objectiveContext
                );

        if(!matcher.find()){
            return null;
        }

        try{
            return Double.valueOf(
                    matcher.group(1)
            );
        }catch(NumberFormatException exception){
            return null;
        }
    }

    private String recommendationFor(
            int score
    ){

        if(score >= 80){
            return "STRONG_FIT";
        }

        if(score >= 65){
            return "POTENTIAL_FIT";
        }

        if(score >= 50){
            return "REVIEW";
        }

        return "WEAK_FIT";
    }

    private String assessmentFor(
            int score
    ){

        if(score >= 80){
            return "Strong candidate with substantial supporting evidence.";
        }

        if(score >= 65){
            return "Promising candidate, but the organizer should verify key evidence.";
        }

        if(score >= 50){
            return "Mixed candidate profile requiring organizer review.";
        }

        return "Limited supporting evidence or significant concerns were detected.";
    }

    private String positionSuitabilityFor(
            int score
    ){

        if(score >= 75){
            return "Good positional suitability based on submitted evidence.";
        }

        if(score >= 50){
            return "Partial positional suitability; additional verification is recommended.";
        }

        return "Positional suitability is currently weak or insufficiently supported.";
    }

    private String strengthFor(
            int score
    ){

        if(score >= 75){
            return "Strong evidence alignment.";
        }

        if(score >= 50){
            return "Some relevant evidence was submitted.";
        }

        return "Candidate information was received for organizer review.";
    }

    private String secondStrengthFor(
            String evidence
    ){

        if(evidence.contains("experience")){
            return "Experience information is available.";
        }

        if(evidence.contains("skill")){
            return "Skill-related information is available.";
        }

        return "Basic application details are available.";
    }

    private String weaknessFor(
            int score
    ){

        if(score >= 75){
            return "Some evidence still requires verification.";
        }

        if(score >= 50){
            return "Evidence is incomplete or mixed.";
        }

        return "Limited evidence supports the candidate profile.";
    }

    private String secondWeaknessFor(
            String evidence
    ){

        if(evidence.contains("no experience") ||
                evidence.contains("none")){
            return "No meaningful experience evidence was detected.";
        }

        return "Organizer verification is required before selection.";
    }

    private String experienceAnalysisFor(
            String evidence
    ){

        if(evidence.contains("experience") &&
                evidence.contains("years")){
            return "Experience duration and related evidence were detected.";
        }

        if(evidence.contains("experience")){
            return "Experience-related information was detected.";
        }

        return "No strong experience evidence was detected.";
    }

    private String concernFor(
            int score
    ){

        if(score >= 80){
            return "Verify submitted evidence before final selection.";
        }

        if(score >= 50){
            return "Review missing or inconsistent candidate details.";
        }

        return "Do not rely on this score without additional evidence.";
    }

    private int clamp(
            int value,
            int minimum,
            int maximum
    ){

        return Math.max(
                minimum,
                Math.min(
                        maximum,
                        value
                )
        );
    }

    private String safe(
            String value
    ){

        return value == null
                ? ""
                : value;
    }

    private String jsonEscape(
            String value
    ){

        return safe(value)
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\n",
                        "\\n"
                );
    }

    @Override
    public String providerName() {
        return "MOCK";
    }

    @Override
public String modelName() {
    return "athletiq-mock-v1";
}
}