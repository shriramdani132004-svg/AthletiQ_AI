package com.athletiq.backend.objectiveevaluation.controller;

import com.athletiq.backend.objectiveevaluation.dto.ObjectiveEvaluationResponse;
import com.athletiq.backend.objectiveevaluation.service.ObjectiveEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class ObjectiveEvaluationController {

    private final ObjectiveEvaluationService service;

    public ObjectiveEvaluationController(
            ObjectiveEvaluationService service
    ) {

        this.service =
                service;
    }

    @PostMapping(
            "/{eventId}/applications/{applicationId}/evaluate"
    )
    public ResponseEntity<ObjectiveEvaluationResponse>
    evaluate(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            Authentication authentication
    ) {

        Long organizerId =
                authenticatedUserId(
                        authentication
                );

        return ResponseEntity.ok(
                service.evaluate(
                        organizerId,
                        eventId,
                        applicationId
                )
        );
    }

    @GetMapping(
            "/{eventId}/applications/{applicationId}/evaluation"
    )
    public ResponseEntity<ObjectiveEvaluationResponse>
    getEvaluation(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            Authentication authentication
    ) {

        Long organizerId =
                authenticatedUserId(
                        authentication
                );

        return ResponseEntity.ok(
                service.getEvaluation(
                        organizerId,
                        eventId,
                        applicationId
                )
        );
    }

    @PostMapping(
            "/{eventId}/applications/{applicationId}/evaluation/recalculate"
    )
    public ResponseEntity<ObjectiveEvaluationResponse>
    recalculate(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            Authentication authentication
    ) {

        Long organizerId =
                authenticatedUserId(
                        authentication
                );

        return ResponseEntity.ok(
                service.recalculate(
                        organizerId,
                        eventId,
                        applicationId
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