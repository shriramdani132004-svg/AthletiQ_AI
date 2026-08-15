package com.athletiq.backend.event.requirements.controller;

import com.athletiq.backend.event.requirements.dto.EventRequirementsRequest;
import com.athletiq.backend.event.requirements.dto.EventRequirementsResponse;
import com.athletiq.backend.event.requirements.service.EventRequirementsService;
import com.athletiq.backend.security.authorization.CanReadEvent;
import com.athletiq.backend.security.authorization.CanUpdateEvent;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/{eventId}/requirements")
public class EventRequirementsController {

    private final EventRequirementsService requirementsService;

    public EventRequirementsController(
            EventRequirementsService requirementsService
    ) {
        this.requirementsService = requirementsService;
    }

    @GetMapping
    @CanReadEvent
    public ResponseEntity<EventRequirementsResponse> get(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);

        return ResponseEntity.ok(
                requirementsService.get(organizerId, eventId)
        );
    }

    @PutMapping
    @CanUpdateEvent
    public ResponseEntity<EventRequirementsResponse> update(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequirementsRequest request,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);

        return ResponseEntity.ok(
                requirementsService.update(
                        organizerId,
                        eventId,
                        request
                )
        );
    }

    private Long authenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException(
                    "Authenticated user is required."
            );
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Authenticated principal must contain a numeric user ID."
            );
        }
    }
}