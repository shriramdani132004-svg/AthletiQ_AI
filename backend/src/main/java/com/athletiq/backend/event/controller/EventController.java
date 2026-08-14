package com.athletiq.backend.event.controller;

import com.athletiq.backend.event.dto.CreateEventRequest;
import com.athletiq.backend.event.dto.EventResponse;
import com.athletiq.backend.event.dto.UpdateEventRequest;
import com.athletiq.backend.event.entity.EventStatus;
import com.athletiq.backend.event.service.EventService;
import com.athletiq.backend.security.authorization.CanCreateEvent;
import com.athletiq.backend.security.authorization.CanReadEvent;
import com.athletiq.backend.security.authorization.CanUpdateEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @CanCreateEvent
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.create(organizerId, request));
    }

    @GetMapping
    @CanReadEvent
    public ResponseEntity<List<EventResponse>> list(Authentication authentication) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.ok(eventService.getOrganizerEvents(organizerId));
    }

    @GetMapping("/{eventId}")
    @CanReadEvent
    public ResponseEntity<EventResponse> get(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.ok(eventService.getOwned(organizerId, eventId));
    }

    @PutMapping("/{eventId}")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> update(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.ok(eventService.update(organizerId, eventId, request));
    }

    @PostMapping("/{eventId}/publish")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> publish(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.PUBLISHED);
    }

    @PostMapping("/{eventId}/applications/open")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> openApplications(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.APPLICATIONS_OPEN);
    }

    @PostMapping("/{eventId}/applications/pause")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> pauseApplications(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.APPLICATIONS_CLOSED);
    }

    @PostMapping("/{eventId}/applications/reopen")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> reopenApplications(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.APPLICATIONS_OPEN);
    }

    @PostMapping("/{eventId}/applications/close")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> closeApplications(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.APPLICATIONS_CLOSED);
    }

    @PostMapping("/{eventId}/archive")
    @CanUpdateEvent
    public ResponseEntity<EventResponse> archive(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return transition(eventId, authentication, EventStatus.ARCHIVED);
    }

    @PostMapping("/{eventId}/duplicate")
    @CanCreateEvent
    public ResponseEntity<EventResponse> duplicate(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.duplicate(organizerId, eventId));
    }

    private ResponseEntity<EventResponse> transition(
            Long eventId,
            Authentication authentication,
            EventStatus targetStatus
    ) {
        Long organizerId = authenticatedUserId(authentication);
        return ResponseEntity.ok(eventService.transition(organizerId, eventId, targetStatus));
    }

    private Long authenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user is required.");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Authenticated principal must contain a numeric user ID.");
        }
    }
}