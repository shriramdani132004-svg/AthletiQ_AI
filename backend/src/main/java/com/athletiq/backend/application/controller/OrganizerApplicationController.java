package com.athletiq.backend.application.controller;

import com.athletiq.backend.application.dto.OrganizerApplicationDetailResponse;

import com.athletiq.backend.application.dto.OrganizerApplicationPageResponse;
import com.athletiq.backend.application.dto.OrganizerApplicationStatisticsResponse;
import com.athletiq.backend.application.entity.ApplicationStatus;
import com.athletiq.backend.application.service.OrganizerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<OrganizerApplicationPageResponse>
    getApplications(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "submittedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication
    ) {

        authenticatedUserId(authentication);

        return ResponseEntity.ok(
                service.getApplications(
                        authenticatedUserId(authentication),
                        eventId,
                        page,
                        size,
                        search,
                        email,
                        age,
                        position,
                        status,
                        sort,
                        direction
                )
        );
    }

    @GetMapping("/{eventId}/applications/{applicationId}")
    public ResponseEntity<OrganizerApplicationDetailResponse>
    getApplicationDetail(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            Authentication authentication
    ) {

        Long organizerId =
                authenticatedUserId(authentication);

        return ResponseEntity.ok(
                service.getApplicationDetail(
                        organizerId,
                        eventId,
                        applicationId
                )
        );
    }
    @GetMapping("/{eventId}/applications/statistics")
    public ResponseEntity<OrganizerApplicationStatisticsResponse>
    getStatistics(
            @PathVariable Long eventId,
            Authentication authentication
    ) {

        authenticatedUserId(authentication);

        return ResponseEntity.ok(
                service.getStatistics(
                        authenticatedUserId(authentication),
                        eventId
                )
        );
    }

    private Long authenticatedUserId(
            Authentication authentication
    ) {

        if(
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

        } catch(NumberFormatException exception) {

            throw new IllegalStateException(
                    "Authenticated principal must contain a numeric user ID."
            );
        }
    }
}