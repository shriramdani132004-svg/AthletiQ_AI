package com.athletiq.backend.event.evaluation.controller;

import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionRequest;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriterionResponse;
import com.athletiq.backend.event.evaluation.dto.EvaluationCriteriaValidationResponse;
import com.athletiq.backend.event.evaluation.service.EvaluationCriterionService;
import com.athletiq.backend.security.authorization.CanReadEvent;
import com.athletiq.backend.security.authorization.CanUpdateEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/evaluation-criteria")
public class EvaluationCriterionController {

    private final EvaluationCriterionService criterionService;

    public EvaluationCriterionController(
            EvaluationCriterionService criterionService
    ) {
        this.criterionService = criterionService;
    }

    @GetMapping
    @CanReadEvent
    public ResponseEntity<List<EvaluationCriterionResponse>> getAll(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                criterionService.getAll(
                        authenticatedUserId(authentication),
                        eventId
                )
        );
    }

    @GetMapping("/validation")
    @CanReadEvent
    public ResponseEntity<EvaluationCriteriaValidationResponse> validate(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                criterionService.validate(
                        authenticatedUserId(authentication),
                        eventId
                )
        );
    }
    @PostMapping
    @CanUpdateEvent
    public ResponseEntity<EvaluationCriterionResponse> create(
            @PathVariable Long eventId,
            @Valid @RequestBody EvaluationCriterionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        criterionService.create(
                                authenticatedUserId(authentication),
                                eventId,
                                request
                        )
                );
    }

    @PutMapping("/{criterionId}")
    @CanUpdateEvent
    public ResponseEntity<EvaluationCriterionResponse> update(
            @PathVariable Long eventId,
            @PathVariable Long criterionId,
            @Valid @RequestBody EvaluationCriterionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                criterionService.update(
                        authenticatedUserId(authentication),
                        eventId,
                        criterionId,
                        request
                )
        );
    }

    @DeleteMapping("/{criterionId}")
    @CanUpdateEvent
    public ResponseEntity<Void> delete(
            @PathVariable Long eventId,
            @PathVariable Long criterionId,
            Authentication authentication
    ) {
        criterionService.delete(
                authenticatedUserId(authentication),
                eventId,
                criterionId
        );

        return ResponseEntity.noContent().build();
    }

    private Long authenticatedUserId(
            Authentication authentication
    ) {
        if (authentication == null ||
                authentication.getName() == null) {
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