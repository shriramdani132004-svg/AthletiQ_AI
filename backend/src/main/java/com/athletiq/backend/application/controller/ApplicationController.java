package com.athletiq.backend.application.controller;

import com.athletiq.backend.application.dto.ApplicationResponse;
import com.athletiq.backend.application.service.ApplicationService;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final EventRepository eventRepository;

    public ApplicationController(
            ApplicationService applicationService,
            EventRepository eventRepository
    ) {
        this.applicationService = applicationService;
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @PathVariable Long eventId,
            @RequestParam Long formVersionId,
            @RequestParam Long applicantId
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found."
                        )
                );

        ApplicationResponse response =
                applicationService.createApplication(
                        event,
                        formVersionId,
                        applicantId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<java.util.List<com.athletiq.backend.application.entity.Application>> getApplications(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByEvent(eventId)
        );
    }
}