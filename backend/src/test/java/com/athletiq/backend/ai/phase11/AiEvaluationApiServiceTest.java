package com.athletiq.backend.ai.phase11;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.evaluation.repository.EvaluationCriterionRepository;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.event.requirements.repository.EventRequirementsRepository;
import com.athletiq.backend.objectiveevaluation.dto.ObjectiveEvaluationResponse;
import com.athletiq.backend.objectiveevaluation.entity.EligibilityStatus;
import com.athletiq.backend.objectiveevaluation.entity.ObjectiveEvaluationStatus;
import com.athletiq.backend.objectiveevaluation.service.ObjectiveEvaluationService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiEvaluationApiServiceTest {

    @Test
    void evaluatesOwnedApplicationThroughRealContextPipeline() {

        Long organizerId = 99L;
        Long eventId = 1L;
        Long applicationId = 10L;

        Event event = new Event();

        event.setId(eventId);
        event.setOrganizerId(organizerId);
        event.setName("AthletiQ Test Event");
        event.setSport("Cricket");
        event.setDescription("Phase 11 API test event.");
        event.setLocation("Test Ground");
        event.setStartDate(
                LocalDate.now().plusDays(10)
        );
        event.setEndDate(
                LocalDate.now().plusDays(11)
        );
        event.setRegistrationDeadline(
                LocalDateTime.now().plusDays(5)
        );
        event.setPlayersRequired(11);
        event.setAgeCategory("19-27");
        event.setEligibilityCriteria(
                "Eligible candidates only."
        );
        event.setEventRules(
                "Standard rules."
        );

        Application application =
                new Application();

        application.setEvent(event);
        application.setApplicantId(501L);
        application.setApplicantName(
                "Test Player"
        );
        application.setApplicantEmail(
                "player@test.local"
        );
        application.setApplicantPhone(
                "9000000000"
        );
        application.setSubmittedData(
                """
                {
                  "age":21,
                  "position":"Batsman",
                  "experience":"3 years",
                  "batting":90,
                  "bowling":80,
                  "fielding":85
                }
                """
        );
        application.setFileMetadata("{}");

        setField(
                application,
                "id",
                applicationId
        );

        ApplicationRepository applicationRepository =
                mock(ApplicationRepository.class);

        EventRequirementsRepository requirementsRepository =
                mock(EventRequirementsRepository.class);

        EvaluationCriterionRepository criterionRepository =
                mock(EvaluationCriterionRepository.class);

        ObjectiveEvaluationService objectiveService =
                mock(ObjectiveEvaluationService.class);

        CandidateContextBuilder contextBuilder =
                mock(CandidateContextBuilder.class);

        AiEvaluationService aiEvaluationService =
                mock(AiEvaluationService.class);

        when(
                applicationRepository.findById(
                        applicationId
                )
        ).thenReturn(
                Optional.of(application)
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

        when(
                requirementsRepository.findByEventId(
                        eventId
                )
        ).thenReturn(
                Optional.of(requirements)
        );

        when(
                criterionRepository
                        .findByEventIdOrderByDisplayOrderAsc(
                                eventId
                        )
        ).thenReturn(
                List.of()
        );

        ObjectiveEvaluationResponse objective =
                new ObjectiveEvaluationResponse(
                        7001L,
                        applicationId,
                        eventId,
                        1L,
                        ObjectiveEvaluationStatus.values()[0],
                        EligibilityStatus.values()[0],
                        BigDecimal.valueOf(84.5),
                        "Eligible",
                        "Objective score calculated.",
                        1,
                        LocalDateTime.now(),
                        List.of()
                );

        when(
                objectiveService.evaluate(
                        organizerId,
                        eventId,
                        applicationId
                )
        ).thenReturn(
                objective
        );

        AiEvaluationProviderRequest request =
                new AiEvaluationProviderRequest(
                        "APPLICATION-10",
                        "candidate context",
                        "requirements context",
                        "application context",
                        "criteria context",
                        "objective context",
                        "prompt-v1"
                );

        when(
                contextBuilder.build(
                        same(application),
                        same(requirements),
                        anyList(),
                        same(objective),
                        eq("prompt-v1")
                )
        ).thenReturn(
                request
        );

        AiEvaluationResult expected =
                new AiEvaluationResult(
                        "APPLICATION-10",
                        82.0,
                        "Strong candidate.",
                        List.of(
                                "Strong batting"
                        ),
                        List.of(
                                "Experience should be verified"
                        ),
                        "Relevant competitive experience.",
                        List.of(),
                        "Good positional fit.",
                        "STRONG_FIT",
                        List.of(),
                        "Advisory AI assessment."
                );

        when(
                aiEvaluationService.evaluate(
                        same(request)
                )
        ).thenReturn(
                expected
        );

        AiEvaluationApiService service =
                new AiEvaluationApiService(
                        applicationRepository,
                        requirementsRepository,
                        criterionRepository,
                        objectiveService,
                        contextBuilder,
                        aiEvaluationService
                );

        AiEvaluationResult actual =
                service.evaluate(
                        organizerId,
                        eventId,
                        applicationId
                );

        assertNotNull(actual);

        assertEquals(
                "APPLICATION-10",
                actual.candidateReference()
        );

        assertEquals(
                82.0,
                actual.score()
        );

        assertEquals(
                "STRONG_FIT",
                actual.recommendation()
        );

        verify(
                applicationRepository
        ).findById(
                applicationId
        );

        verify(
                objectiveService
        ).evaluate(
                organizerId,
                eventId,
                applicationId
        );

        verify(
                contextBuilder
        ).build(
                same(application),
                same(requirements),
                anyList(),
                same(objective),
                eq("prompt-v1")
        );

        verify(
                aiEvaluationService
        ).evaluate(
                same(request)
        );
    }

    private static void setField(
            Object target,
            String name,
            Object value
    ){

        try {

            Field field =
                    target
                            .getClass()
                            .getDeclaredField(name);

            field.setAccessible(true);
            field.set(target, value);

        } catch(Exception exception) {

            throw new IllegalStateException(
                    "Unable to set test field: " + name,
                    exception
            );
        }
    }
}