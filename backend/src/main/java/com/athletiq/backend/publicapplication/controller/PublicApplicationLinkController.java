package com.athletiq.backend.publicapplication.controller;

import com.athletiq.backend.publicapplication.dto.PublicApplicationLinkResponse;
import com.athletiq.backend.publicapplication.service.PublicApplicationLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class PublicApplicationLinkController {

    private final PublicApplicationLinkService service;

    public PublicApplicationLinkController(
            PublicApplicationLinkService service
    ) {
        this.service = service;
    }

    @PostMapping("/{eventId}/public-application")
    public ResponseEntity<PublicApplicationLinkResponse> generateOrGet(
            @PathVariable Long eventId,
            @RequestParam(required = false, defaultValue = "http://localhost:5173")
            String publicBaseUrl,
            Authentication authentication
    ) {
        Long organizerId = authenticatedUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        service.generateOrGet(
                                organizerId,
                                eventId,
                                publicBaseUrl
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