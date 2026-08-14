package com.athletiq.backend.event.service;

import com.athletiq.backend.event.dto.CreateEventRequest;
import com.athletiq.backend.event.dto.EventResponse;
import com.athletiq.backend.event.dto.UpdateEventRequest;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.entity.EventStatus;
import com.athletiq.backend.event.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse create(Long organizerId, CreateEventRequest request) {
        validateEventDates(request.startDate(), request.endDate(), request.registrationDeadline());
        Event event = new Event();
        event.setOrganizerId(organizerId);
        event.setName(request.name().trim());
        event.setSport(request.sport().trim());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setRegistrationDeadline(request.registrationDeadline());
        event.setPlayersRequired(request.playersRequired());
        event.setAgeCategory(request.ageCategory());
        event.setEligibilityCriteria(request.eligibilityCriteria());
        event.setEventRules(request.eventRules());
        event.setBannerUrl(request.bannerUrl());
        event.setStatus(EventStatus.DRAFT);
        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponse getOwned(Long organizerId, Long eventId) {
        return toResponse(findOwnedEntity(organizerId, eventId));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getOrganizerEvents(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EventResponse update(Long organizerId, Long eventId, UpdateEventRequest request) {
        Event event = findOwnedEntity(organizerId, eventId);
        ensureDraft(event);
        validateEventDates(request.startDate(), request.endDate(), request.registrationDeadline());
        event.setName(request.name().trim());
        event.setSport(request.sport().trim());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setRegistrationDeadline(request.registrationDeadline());
        event.setPlayersRequired(request.playersRequired());
        event.setAgeCategory(request.ageCategory());
        event.setEligibilityCriteria(request.eligibilityCriteria());
        event.setEventRules(request.eventRules());
        event.setBannerUrl(request.bannerUrl());
        event.setUpdatedAt(LocalDateTime.now());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse transition(Long organizerId, Long eventId, EventStatus targetStatus) {
        Event event = findOwnedEntity(organizerId, eventId);
        validateTransition(event, targetStatus);
        event.setStatus(targetStatus);
        event.setUpdatedAt(LocalDateTime.now());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse duplicate(Long organizerId, Long eventId) {
        Event source = findOwnedEntity(organizerId, eventId);
        Event copy = new Event();
        copy.setOrganizerId(organizerId);
        copy.setName(source.getName() + " - Copy");
        copy.setSport(source.getSport());
        copy.setDescription(source.getDescription());
        copy.setLocation(source.getLocation());
        copy.setStartDate(source.getStartDate());
        copy.setEndDate(source.getEndDate());
        copy.setRegistrationDeadline(source.getRegistrationDeadline());
        copy.setPlayersRequired(source.getPlayersRequired());
        copy.setAgeCategory(source.getAgeCategory());
        copy.setEligibilityCriteria(source.getEligibilityCriteria());
        copy.setEventRules(source.getEventRules());
        copy.setBannerUrl(source.getBannerUrl());
        copy.setStatus(EventStatus.DRAFT);
        LocalDateTime now = LocalDateTime.now();
        copy.setCreatedAt(now);
        copy.setUpdatedAt(now);
        return toResponse(eventRepository.save(copy));
    }

    private Event findOwnedEntity(Long organizerId, Long eventId) {
        return eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found or access denied."));
    }

    private void ensureDraft(Event event) {
        if (event.getStatus() != EventStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT events can be edited.");
        }
    }

    private void validateEventDates(LocalDate startDate, LocalDate endDate, LocalDateTime registrationDeadline) {
        if (startDate == null || endDate == null || registrationDeadline == null) {
            throw new IllegalArgumentException("Event dates and registration deadline are required.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Event end date cannot be before start date.");
        }
        if (registrationDeadline.toLocalDate().isAfter(startDate)) {
            throw new IllegalArgumentException("Registration deadline cannot be after the event start date.");
        }
    }

    private void validateTransition(Event event, EventStatus targetStatus) {
        EventStatus current = event.getStatus();
        if (targetStatus == null) {
            throw new IllegalArgumentException("Target event status is required.");
        }
        if (!isValidTransition(current, targetStatus)) {
            throw new IllegalStateException("Invalid event lifecycle transition: " + current + " -> " + targetStatus);
        }
    }

    private boolean isValidTransition(EventStatus current, EventStatus target) {
        return switch (current) {
            case DRAFT -> target == EventStatus.PUBLISHED;
            case PUBLISHED -> target == EventStatus.APPLICATIONS_OPEN || target == EventStatus.ARCHIVED;
            case APPLICATIONS_OPEN -> target == EventStatus.APPLICATIONS_CLOSED;
            case APPLICATIONS_CLOSED -> target == EventStatus.APPLICATIONS_OPEN || target == EventStatus.SELECTION || target == EventStatus.ARCHIVED;
            case SELECTION -> target == EventStatus.COMPLETED;
            case COMPLETED -> target == EventStatus.ARCHIVED;
            case ARCHIVED -> false;
        };
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getOrganizerId(),
                event.getName(),
                event.getSport(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDate(),
                event.getEndDate(),
                event.getRegistrationDeadline(),
                event.getPlayersRequired(),
                event.getAgeCategory(),
                event.getEligibilityCriteria(),
                event.getEventRules(),
                event.getBannerUrl(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}