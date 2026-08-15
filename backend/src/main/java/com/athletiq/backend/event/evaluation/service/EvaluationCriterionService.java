package com.athletiq.backend.event.evaluation.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriteriaValidationResponse;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionRequest;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionResponse;
import com.athletiq.backend.event.evaluation.entity.EvaluationCriterion;
import com.athletiq.backend.event.evaluation.repository.EvaluationCriterionRepository;
import com.athletiq.backend.event.repository.EventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EvaluationCriterionService {

    private static final BigDecimal TOTAL_WEIGHT = new BigDecimal("100.00");

    private final EventRepository eventRepository;
    private final EvaluationCriterionRepository criterionRepository;

    public EvaluationCriterionService(
            EventRepository eventRepository,
            EvaluationCriterionRepository criterionRepository
    ) {
        this.eventRepository = eventRepository;
        this.criterionRepository = criterionRepository;
    }

    @Transactional(readOnly = true)
    public List<EvaluationCriterionResponse> getAll(
            Long organizerId,
            Long eventId
    ) {
        requireOwnedEvent(organizerId, eventId);

        return criterionRepository
                .findByEventIdOrderByDisplayOrderAsc(eventId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvaluationCriteriaValidationResponse validate(
            Long organizerId,
            Long eventId
    ) {
        requireOwnedEvent(organizerId, eventId);

        BigDecimal total = activeWeightTotal(eventId);

        boolean valid = total.compareTo(TOTAL_WEIGHT) == 0;

        String message = valid
                ? "Evaluation criteria weights are valid."
                : "Active evaluation criteria weights must total exactly 100%.";

        return new EvaluationCriteriaValidationResponse(
                valid,
                total,
                message
        );
    }

    @Transactional
    public EvaluationCriterionResponse create(
            Long organizerId,
            Long eventId,
            EvaluationCriterionRequest request
    ) {
        Event event = requireOwnedEvent(organizerId, eventId);

        validateScores(request.minScore(), request.maxScore());
        validateWeight(request.weight());
        validateNameUniqueness(eventId, request.name(), null);

        boolean enabled =
                request.enabled() == null || request.enabled();

        if (enabled) {
            validateResultingActiveTotal(
                    eventId,
                    null,
                    request.weight()
            );
        }

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setEvent(event);
        applyRequest(criterion, request);

        try {
            return toResponse(criterionRepository.save(criterion));
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(
                    "A criterion with this name already exists for the event."
            );
        }
    }

    @Transactional
    public EvaluationCriterionResponse update(
            Long organizerId,
            Long eventId,
            Long criterionId,
            EvaluationCriterionRequest request
    ) {
        requireOwnedEvent(organizerId, eventId);

        EvaluationCriterion criterion =
                criterionRepository.findById(criterionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evaluation criterion not found."
                                )
                        );

        if (!criterion.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException(
                    "Evaluation criterion does not belong to this event."
            );
        }

        validateScores(request.minScore(), request.maxScore());
        validateWeight(request.weight());
        validateNameUniqueness(
                eventId,
                request.name(),
                criterionId
        );

        boolean enabled =
                request.enabled() == null || request.enabled();

        if (enabled) {
            validateResultingActiveTotal(
                    eventId,
                    criterion,
                    request.weight()
            );
        } else if (criterion.isEnabled()) {
            BigDecimal resultingTotal =
                    activeWeightTotal(eventId)
                            .subtract(criterion.getWeight())
                            .setScale(2, RoundingMode.HALF_UP);

            if (resultingTotal.compareTo(TOTAL_WEIGHT) > 0) {
                throw new IllegalArgumentException(
                        "Invalid active weight total."
                );
            }
        }

        applyRequest(criterion, request);

        try {
            return toResponse(criterionRepository.save(criterion));
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(
                    "A criterion with this name already exists for the event."
            );
        }
    }

    @Transactional
    public void delete(
            Long organizerId,
            Long eventId,
            Long criterionId
    ) {
        requireOwnedEvent(organizerId, eventId);

        EvaluationCriterion criterion =
                criterionRepository.findById(criterionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Evaluation criterion not found."
                                )
                        );

        if (!criterion.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException(
                    "Evaluation criterion does not belong to this event."
            );
        }

        criterionRepository.delete(criterion);
    }

    private void validateResultingActiveTotal(
            Long eventId,
            EvaluationCriterion currentCriterion,
            BigDecimal requestedWeight
    ) {
        BigDecimal currentTotal = activeWeightTotal(eventId);

        if (currentCriterion != null &&
                currentCriterion.isEnabled()) {
            currentTotal =
                    currentTotal.subtract(
                            currentCriterion.getWeight()
                    );
        }

        BigDecimal resultingTotal =
                currentTotal
                        .add(requestedWeight)
                        .setScale(2, RoundingMode.HALF_UP);

        if (resultingTotal.compareTo(TOTAL_WEIGHT) > 0) {
            throw new IllegalArgumentException(
                    "Active evaluation criteria weights cannot exceed 100%."
            );
        }
    }

    private BigDecimal activeWeightTotal(Long eventId) {
        return criterionRepository
                .findByEventIdOrderByDisplayOrderAsc(eventId)
                .stream()
                .filter(EvaluationCriterion::isEnabled)
                .map(EvaluationCriterion::getWeight)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                )
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateWeight(BigDecimal weight) {
        if (weight == null) {
            throw new IllegalArgumentException(
                    "Criterion weight is required."
            );
        }

        if (weight.compareTo(BigDecimal.ZERO) <= 0 ||
                weight.compareTo(TOTAL_WEIGHT) > 0) {
            throw new IllegalArgumentException(
                    "Criterion weight must be greater than 0 and at most 100."
            );
        }
    }

    private Event requireOwnedEvent(
            Long organizerId,
            Long eventId
    ) {
        if (organizerId == null) {
            throw new IllegalArgumentException(
                    "Organizer ID is required."
            );
        }

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        return eventRepository.findByIdAndOrganizerId(
                        eventId,
                        organizerId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found or access denied."
                        )
                );
    }

    private void validateScores(
            BigDecimal minScore,
            BigDecimal maxScore
    ) {
        if (minScore == null || maxScore == null) {
            throw new IllegalArgumentException(
                    "Minimum and maximum scores are required."
            );
        }

        if (minScore.compareTo(maxScore) > 0) {
            throw new IllegalArgumentException(
                    "Minimum score cannot be greater than maximum score."
            );
        }
    }

    private void validateNameUniqueness(
            Long eventId,
            String name,
            Long currentCriterionId
    ) {
        String normalizedName =
                name == null ? null : name.trim();

        if (normalizedName == null ||
                normalizedName.isBlank()) {
            throw new IllegalArgumentException(
                    "Criterion name is required."
            );
        }

        boolean exists =
                criterionRepository.existsByEventIdAndNameIgnoreCase(
                        eventId,
                        normalizedName
                );

        if (!exists) {
            return;
        }

        if (currentCriterionId != null) {
            EvaluationCriterion existing =
                    criterionRepository
                            .findByEventIdOrderByDisplayOrderAsc(eventId)
                            .stream()
                            .filter(c ->
                                    c.getName()
                                            .equalsIgnoreCase(
                                                    normalizedName
                                            )
                            )
                            .findFirst()
                            .orElse(null);

            if (existing != null &&
                    existing.getId().equals(currentCriterionId)) {
                return;
            }
        }

        throw new IllegalArgumentException(
                "A criterion with this name already exists for the event."
        );
    }

    private void applyRequest(
            EvaluationCriterion criterion,
            EvaluationCriterionRequest request
    ) {
        criterion.setName(request.name().trim());
        criterion.setDescription(trim(request.description()));
        criterion.setWeight(request.weight());
        criterion.setMinScore(request.minScore());
        criterion.setMaxScore(request.maxScore());
        criterion.setCriterionType(request.criterionType());
        criterion.setEnabled(
                request.enabled() == null || request.enabled()
        );
        criterion.setDisplayOrder(request.displayOrder());
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private EvaluationCriterionResponse toResponse(
            EvaluationCriterion criterion
    ) {
        return new EvaluationCriterionResponse(
                criterion.getId(),
                criterion.getEvent().getId(),
                criterion.getName(),
                criterion.getDescription(),
                criterion.getWeight(),
                criterion.getMinScore(),
                criterion.getMaxScore(),
                criterion.getCriterionType(),
                criterion.isEnabled(),
                criterion.getDisplayOrder(),
                criterion.getCreatedAt(),
                criterion.getUpdatedAt()
        );
    }
}