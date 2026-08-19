package com.athletiq.backend.ai.phase11;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/{eventId}/applications")
public class AiEvaluationApiController {

    private final AiEvaluationApiService aiEvaluationApiService;

    public AiEvaluationApiController(
            AiEvaluationApiService aiEvaluationApiService
    ) {
        this.aiEvaluationApiService =
                aiEvaluationApiService;
    }

    @PostMapping("/{applicationId}/ai-evaluation")
    public ResponseEntity<AiEvaluationResult> evaluate(
            @PathVariable Long eventId,
            @PathVariable Long applicationId,
            @RequestParam Long organizerId
    ) {

        return ResponseEntity.ok(
                aiEvaluationApiService.evaluate(
                        organizerId,
                        eventId,
                        applicationId
                )
        );
    }
}