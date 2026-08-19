package com.athletiq.backend.ai.phase11;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveEvaluationResponse;
import com.athletiq.backend.objectiveevaluation.entity.EligibilityStatus;
import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluationStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateContextBuilderTest {

    @Test
    void buildsRealCandidateContext() {

        Application application =
                new Application();

        try {
            java.lang.reflect.Field idField =
                    Application.class.getDeclaredField("id");

            idField.setAccessible(true);
            idField.set(application, 10L);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not assign test application ID.",
                    exception
            );
        }

        application.setApplicantName(
                "Test Player"
        );

        application.setApplicantEmail(
                "test@athletiq.test"
        );

        application.setSubmittedData(
                "{\"age\":21,\"position\":\"Batsman\",\"batting\":90}"
        );

        EventRequirements requirements =
                new EventRequirements();

        requirements.setRequiredPositions(
                "Batsman, All-rounder"
        );

        requirements.setMinAge(19);
        requirements.setMaxAge(27);

        requirements.setMinimumExperience(
                "At least 3 years"
        );

        requirements.setRequiredSkills(
                "Batting, fielding, match awareness"
        );

        EvaluationCriterion batting =
                new EvaluationCriterion();

        batting.setName("Batting");
        batting.setDescription(
                "Batting evaluation"
        );
        batting.setWeight(
                BigDecimal.valueOf(30)
        );
        batting.setMinScore(
                BigDecimal.ZERO
        );
        batting.setMaxScore(
                BigDecimal.valueOf(100)
        );
        batting.setDisplayOrder(0);
        batting.setEnabled(true);

        ObjectiveEvaluationResponse objective =
                new ObjectiveEvaluationResponse(
                        1L,
                        10L,
                        1L,
                        1L,
                        ObjectiveEvaluationStatus.values()[0],
                        EligibilityStatus.ELIGIBLE,
                        BigDecimal.valueOf(84.5),
                        "Eligible",
                        "Objective score calculated.",
                        1,
                        LocalDateTime.now(),
                        List.of()
                );

        CandidateContextBuilder builder =
                new CandidateContextBuilder();

        AiEvaluationProviderRequest request =
                builder.build(
                        application,
                        requirements,
                        List.of(batting),
                        objective,
                        "prompt-v1"
                );

        assertEquals(
                "APPLICATION-10",
                request.candidateReference()
        );

        assertTrue(
                request.candidateContext()
                        .contains("Test Player")
        );

        assertTrue(
                request.candidateContext()
                        .contains("21")
        );

        assertTrue(
                request.requirementsContext()
                        .contains("Batsman, All-rounder")
        );

        assertTrue(
                request.criteriaContext()
                        .contains("Batting")
        );

        assertTrue(
                request.objectiveContext()
                        .contains("84.5")
        );

        assertEquals(
                "prompt-v1",
                request.promptVersion()
        );
    }
}