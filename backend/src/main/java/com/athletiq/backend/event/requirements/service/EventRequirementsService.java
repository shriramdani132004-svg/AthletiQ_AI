package com.athletiq.backend.event.requirements.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import com.athletiq.backend.event.requirements.dto.EventRequirementsRequest;
import com.athletiq.backend.event.requirements.dto.EventRequirementsResponse;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.event.requirements.repository.EventRequirementsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventRequirementsService {

    private final EventRepository eventRepository;
    private final EventRequirementsRepository requirementsRepository;

    public EventRequirementsService(
            EventRepository eventRepository,
            EventRequirementsRepository requirementsRepository
    ) {
        this.eventRepository = eventRepository;
        this.requirementsRepository = requirementsRepository;
    }

    @Transactional(readOnly = true)
    public EventRequirementsResponse get(Long organizerId, Long eventId) {
        Event event = findOwnedEvent(organizerId, eventId);

        return requirementsRepository.findByEventId(event.getId())
                .map(this::toResponse)
                .orElseGet(() -> emptyResponse(event.getId()));
    }

    @Transactional
    public EventRequirementsResponse update(
            Long organizerId,
            Long eventId,
            EventRequirementsRequest request
    ) {
        Event event = findOwnedEvent(organizerId, eventId);

        validateAgeRange(request.minAge(), request.maxAge());

        EventRequirements requirements =
                requirementsRepository.findByEventId(event.getId())
                        .orElseGet(() -> {
                            EventRequirements created = new EventRequirements();
                            created.setEvent(event);
                            return created;
                        });

        requirements.setRequiredPositions(trim(request.requiredPositions()));
        requirements.setMinAge(request.minAge());
        requirements.setMaxAge(request.maxAge());
        requirements.setMinimumExperience(trim(request.minimumExperience()));
        requirements.setRequiredAchievements(trim(request.requiredAchievements()));
        requirements.setRequiredSkills(trim(request.requiredSkills()));
        requirements.setPerformanceRequirements(trim(request.performanceRequirements()));
        requirements.setFitnessRequirements(trim(request.fitnessRequirements()));
        requirements.setAvailabilityRequirements(trim(request.availabilityRequirements()));
        requirements.setEligibilityConditions(trim(request.eligibilityConditions()));
        requirements.setEventSpecificRequirements(trim(request.eventSpecificRequirements()));

        return toResponse(requirementsRepository.save(requirements));
    }

    private Event findOwnedEvent(Long organizerId, Long eventId) {
        if (organizerId == null) {
            throw new IllegalArgumentException("Organizer ID is required.");
        }

        if (eventId == null) {
            throw new IllegalArgumentException("Event ID is required.");
        }

        return eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found or access denied."
                        )
                );
    }

    private void validateAgeRange(Integer minAge, Integer maxAge) {
        if (minAge != null && maxAge != null && maxAge < minAge) {
            throw new IllegalArgumentException(
                    "Maximum age cannot be less than minimum age."
            );
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private EventRequirementsResponse emptyResponse(Long eventId) {
        return new EventRequirementsResponse(
                null,
                eventId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private EventRequirementsResponse toResponse(EventRequirements requirements) {
        return new EventRequirementsResponse(
                requirements.getId(),
                requirements.getEvent().getId(),
                requirements.getRequiredPositions(),
                requirements.getMinAge(),
                requirements.getMaxAge(),
                requirements.getMinimumExperience(),
                requirements.getRequiredAchievements(),
                requirements.getRequiredSkills(),
                requirements.getPerformanceRequirements(),
                requirements.getFitnessRequirements(),
                requirements.getAvailabilityRequirements(),
                requirements.getEligibilityConditions(),
                requirements.getEventSpecificRequirements(),
                requirements.getCreatedAt(),
                requirements.getUpdatedAt()
        );
    }
}