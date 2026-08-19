package com.athletiq.backend.application.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.athletiq.backend.application.dto.SelectionDecisionRequest;
import com.athletiq.backend.application.dto.SelectionEmailRequest;
import com.athletiq.backend.application.service.OrganizerSelectionService;

@RestController
@RequestMapping("/api/v1/organizer/applications")
public class OrganizerSelectionController {

    private final OrganizerSelectionService selectionService;

    public OrganizerSelectionController(
            OrganizerSelectionService selectionService
    ) {
        this.selectionService =
                selectionService;
    }

    @PostMapping("/{applicationId}/selection")
    @PreAuthorize("hasAuthority('PLAYER_SELECT')")
    public ResponseEntity<Map<String, Object>> decide(
            @PathVariable Long applicationId,
            @RequestBody SelectionDecisionRequest request,
            Authentication authentication
    ) {
        Long organizerId =
                authenticatedOrganizerId(
                        authentication
                );

        return ResponseEntity.ok(
                selectionService.decide(
                        organizerId,
                        applicationId,
                        request
                )
        );
    }

    @PostMapping("/{applicationId}/selection-email")
    @PreAuthorize("hasAuthority('PLAYER_SELECT')")
    public ResponseEntity<Map<String, Object>> sendEmail(
            @PathVariable Long applicationId,
            @RequestBody SelectionEmailRequest request,
            Authentication authentication
    ) {
        Long organizerId =
                authenticatedOrganizerId(
                        authentication
                );

        return ResponseEntity.ok(
                selectionService.sendManualEmail(
                        organizerId,
                        applicationId,
                        request
                )
        );
    }

    @GetMapping("/{applicationId}/selection-email/status")
    @PreAuthorize("hasAuthority('PLAYER_SELECT')")
    public ResponseEntity<Map<String, Object>> emailStatus(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {
        Long organizerId =
                authenticatedOrganizerId(
                        authentication
                );

        return ResponseEntity.ok(
                selectionService.getEmailStatus(
                        organizerId,
                        applicationId
                )
        );
    }

    private Long authenticatedOrganizerId(
            Authentication authentication
    ) {
        if (authentication == null ||
                authentication.getName() == null) {
            throw new IllegalArgumentException(
                    "Authentication is required."
            );
        }

        return Long.valueOf(
                authentication.getName()
        );
    }
}