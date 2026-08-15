package com.athletiq.backend.application.controller;

import com.athletiq.backend.application.dto.OrganizerApplicationResponse;
import com.athletiq.backend.application.service.OrganizerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class OrganizerApplicationController {

    private final OrganizerApplicationService service;

    public OrganizerApplicationController(
            OrganizerApplicationService service
    ) {
        this.service = service;
    }

    @GetMapping("/{eventId}/applications")
    public ResponseEntity<List<OrganizerApplicationResponse>>
    getApplications(
            @PathVariable Long eventId,
            Authentication authentication
    ) {

        Long organizerId =
                authenticatedUserId(authentication);

        return ResponseEntity.ok(
                service.getApplications(
                        organizerId,
                        eventId
                )
        );
    }

    private Long authenticatedUserId(
            Authentication authentication
    ) {

        if (
                authentication == null ||
                authentication.getName() == null
        ) {
            throw new IllegalStateException(
                    "Authenticated user is required."
            );
        }

        try {

            return Long.valueOf(
                    authentication.getName()
            );

        } catch (NumberFormatException exception) {

            throw new IllegalStateException(
                    "Authenticated principal must contain a numeric user ID."
            );
        }
    }
}