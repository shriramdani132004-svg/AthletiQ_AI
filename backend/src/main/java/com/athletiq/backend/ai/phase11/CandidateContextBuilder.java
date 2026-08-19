package com.athletiq.backend.ai.phase11;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveEvaluationResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CandidateContextBuilder {

    public AiEvaluationProviderRequest build(
            Application application,
            EventRequirements requirements,
            List<EvaluationCriterion> criteria,
            ObjectiveEvaluationResponse objective,
            String promptVersion
    ) {

        if(application == null){
            throw new IllegalArgumentException(
                    "Application is required."
            );
        }

        if(requirements == null){
            throw new IllegalArgumentException(
                    "Event requirements are required."
            );
        }

        if(criteria == null){
            throw new IllegalArgumentException(
                    "Evaluation criteria are required."
            );
        }

        if(objective == null){
            throw new IllegalArgumentException(
                    "Objective evaluation is required."
            );
        }

        String candidateContext =
                buildCandidateContext(
                        application
                );

        String requirementsContext =
                buildRequirementsContext(
                        requirements
                );

        String criteriaContext =
                buildCriteriaContext(
                        criteria
                );

        String objectiveContext =
                buildObjectiveContext(
                        objective
                );

        return new AiEvaluationProviderRequest(
                "APPLICATION-" + application.getId(),
                candidateContext,
                requirementsContext,
                application.getSubmittedData(),
                criteriaContext,
                objectiveContext,
                promptVersion
        );
    }

    private String buildCandidateContext(
            Application application
    ){

        return String.join(
                "\n",
                "Candidate name: " +
                        safe(application.getApplicantName()),
                "Candidate email: " +
                        safe(application.getApplicantEmail()),
                "Application answers: " +
                        safe(application.getSubmittedData())
        );
    }

    private String buildRequirementsContext(
            EventRequirements requirements
    ){

        return String.join(
                "\n",
                "Required positions: " +
                        safe(requirements.getRequiredPositions()),
                "Minimum age: " +
                        safe(requirements.getMinAge()),
                "Maximum age: " +
                        safe(requirements.getMaxAge()),
                "Minimum experience: " +
                        safe(requirements.getMinimumExperience()),
                "Required achievements: " +
                        safe(requirements.getRequiredAchievements()),
                "Required skills: " +
                        safe(requirements.getRequiredSkills()),
                "Performance requirements: " +
                        safe(requirements.getPerformanceRequirements()),
                "Fitness requirements: " +
                        safe(requirements.getFitnessRequirements()),
                "Availability requirements: " +
                        safe(requirements.getAvailabilityRequirements()),
                "Eligibility conditions: " +
                        safe(requirements.getEligibilityConditions()),
                "Event-specific requirements: " +
                        safe(requirements.getEventSpecificRequirements())
        );
    }

    private String buildCriteriaContext(
            List<EvaluationCriterion> criteria
    ){

        StringBuilder builder =
                new StringBuilder();

        for(EvaluationCriterion criterion : criteria){

            if(criterion == null){
                continue;
            }

            builder
                    .append("Criterion: ")
                    .append(
                            safe(
                                    criterion.getName()
                            )
                    )
                    .append("\n");

            builder
                    .append("Description: ")
                    .append(
                            safe(
                                    criterion.getDescription()
                            )
                    )
                    .append("\n");

            builder
                    .append("Weight: ")
                    .append(
                            safe(
                                    criterion.getWeight()
                            )
                    )
                    .append("\n");

            builder
                    .append("Minimum score: ")
                    .append(
                            safe(
                                    criterion.getMinScore()
                            )
                    )
                    .append("\n");

            builder
                    .append("Maximum score: ")
                    .append(
                            safe(
                                    criterion.getMaxScore()
                            )
                    )
                    .append("\n");

            builder
                    .append("Enabled: ")
                    .append(
                            criterion.isEnabled()
                    )
                    .append("\n\n");
        }

        return builder.toString().trim();
    }

    private String buildObjectiveContext(
            ObjectiveEvaluationResponse objective
    ){

        BigDecimal score =
                objective.objectiveScore();

        return String.join(
                "\n",
                "Objective status: " +
                        safe(objective.status()),
                "Eligibility status: " +
                        safe(objective.eligibilityStatus()),
                "Objective score: " +
                        safe(score),
                "Eligibility explanation: " +
                        safe(objective.eligibilityExplanation()),
                "Score explanation: " +
                        safe(objective.scoreExplanation())
        );
    }

    private String safe(
            Object value
    ){

        return value == null
                ? ""
                : String.valueOf(value);
    }
}