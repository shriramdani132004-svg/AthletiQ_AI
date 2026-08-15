package com.athletiq.backend.event.evaluation.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionRequest;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionResponse;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriteriaValidationResponse;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterionType;
import com.athletiq.backend.event.evaluation.repository.EvaluationCriterionRepository;
import com.athletiq.backend.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationCriterionServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EvaluationCriterionRepository criterionRepository;

    @InjectMocks
    private EvaluationCriterionService service;

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setOrganizerId(5L);
        event.setName("Test Tournament");
        event.setSport("Football");
    }

    @Test
    void activeWeightTotal65IsInvalid() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.findByEventIdOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(
                        criterion("Batting", "30"),
                        criterion("Bowling", "20"),
                        criterion("Fielding", "15")
                ));

        EvaluationCriteriaValidationResponse result =
                service.validate(5L, 1L);

        assertFalse(result.valid());
        assertEquals(
                new BigDecimal("65.00"),
                result.activeWeightTotal()
        );
    }

    @Test
    void activeWeightTotal100IsValid() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.findByEventIdOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(
                        criterion("Batting", "30"),
                        criterion("Bowling", "20"),
                        criterion("Fielding", "15"),
                        criterion("Experience", "15"),
                        criterion("Fitness", "10"),
                        criterion("Recent Form", "10")
                ));

        EvaluationCriteriaValidationResponse result =
                service.validate(5L, 1L);

        assertTrue(result.valid());
        assertEquals(
                new BigDecimal("100.00"),
                result.activeWeightTotal()
        );
    }

    @Test
    void disabledCriteriaAreExcludedFromTotal() {
        EvaluationCriterion disabled =
                criterion("Fitness", "35");
        disabled.setEnabled(false);

        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.findByEventIdOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(
                        criterion("Batting", "30"),
                        criterion("Bowling", "20"),
                        criterion("Fielding", "15"),
                        disabled
                ));

        EvaluationCriteriaValidationResponse result =
                service.validate(5L, 1L);

        assertFalse(result.valid());
        assertEquals(
                new BigDecimal("65.00"),
                result.activeWeightTotal()
        );
    }

    @Test
    void createRejectsWeightOverRemaining100Percent() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.findByEventIdOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(
                        criterion("Batting", "60"),
                        criterion("Bowling", "30")
                ));

        when(criterionRepository.existsByEventIdAndNameIgnoreCase(
                eq(1L),
                eq("Fitness")
        )).thenReturn(false);

        EvaluationCriterionRequest request =
                request(
                        "Fitness",
                        "20",
                        true,
                        2
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.create(5L, 1L, request)
                );

        assertEquals(
                "Active evaluation criteria weights cannot exceed 100%.",
                exception.getMessage()
        );

        verify(criterionRepository, never())
                .save(any(EvaluationCriterion.class));
    }

    @Test
    void createAllowsWeightWhenTotalDoesNotExceed100() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.findByEventIdOrderByDisplayOrderAsc(1L))
                .thenReturn(List.of(
                        criterion("Batting", "60"),
                        criterion("Bowling", "30")
                ));

        when(criterionRepository.existsByEventIdAndNameIgnoreCase(
                eq(1L),
                eq("Fitness")
        )).thenReturn(false);

        when(criterionRepository.save(any(EvaluationCriterion.class)))
                .thenAnswer(invocation -> {
                    EvaluationCriterion criterion =
                            invocation.getArgument(0);
                    return criterion;
                });

        EvaluationCriterionResponse result =
                service.create(
                        5L,
                        1L,
                        request(
                                "Fitness",
                                "10",
                                true,
                                2
                        )
                );        assertEquals(
                new BigDecimal("10"),
                result.weight()
        );
    }

    @Test
    void duplicateCriterionNameIsRejected() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(criterionRepository.existsByEventIdAndNameIgnoreCase(
                eq(1L),
                eq("Batting")
        )).thenReturn(true);

        EvaluationCriterionRequest request =
                request(
                        "Batting",
                        "20",
                        true,
                        0
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.create(5L, 1L, request)
                );

        assertEquals(
                "A criterion with this name already exists for the event.",
                exception.getMessage()
        );
    }

    @Test
    void invalidScoreRangeIsRejected() {
        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        EvaluationCriterionRequest invalidRequest =
                new EvaluationCriterionRequest(
                        "Fitness",
                        "Fitness evaluation",
                        new BigDecimal("10"),
                        new BigDecimal("100"),
                        new BigDecimal("10"),
                        EvaluationCriterionType.NUMERIC,
                        true,
                        0
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.create(
                                5L,
                                1L,
                                invalidRequest
                        )
                );

        assertEquals(
                "Minimum score cannot be greater than maximum score.",
                exception.getMessage()
        );

        verify(criterionRepository, never())
                .save(any(EvaluationCriterion.class));
    }

    private EvaluationCriterion criterion(
            String name,
            String weight
    ) {
        EvaluationCriterion criterion =
                new EvaluationCriterion();        criterion.setEvent(event);
        criterion.setName(name);
        criterion.setWeight(new BigDecimal(weight));
        criterion.setMinScore(BigDecimal.ZERO);
        criterion.setMaxScore(new BigDecimal("100"));
        criterion.setCriterionType(
                EvaluationCriterionType.NUMERIC
        );
        criterion.setEnabled(true);
        criterion.setDisplayOrder(0);

        return criterion;
    }

    private EvaluationCriterionRequest request(
            String name,
            String weight,
            boolean enabled,
            int order
    ) {
        return new EvaluationCriterionRequest(
                name,
                name + " evaluation",
                new BigDecimal(weight),
                BigDecimal.ZERO,
                new BigDecimal("100"),
                EvaluationCriterionType.NUMERIC,
                enabled,
                order
        );
    }
}