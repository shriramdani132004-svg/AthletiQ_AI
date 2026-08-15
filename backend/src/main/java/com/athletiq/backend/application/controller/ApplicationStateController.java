package com.athletiq.backend.application.controller;

import com.athletiq.backend.application.entity.ApplicationStatus;
import com.athletiq.backend.application.service.ApplicationStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationStateController {

    private final ApplicationStateService stateService;

    public ApplicationStateController(
            ApplicationStateService stateService
    ) {
        this.stateService =
                stateService;
    }

    @PostMapping("/{applicationId}/status")
    public ResponseEntity<Map<String, Object>> transition(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus targetStatus
    ) {

        ApplicationStatus status =
                stateService.transition(
                        applicationId,
                        targetStatus
                );

        return ResponseEntity.ok(
                Map.of(
                        "applicationId",
                        applicationId,
                        "status",
                        status.name()
                )
        );
    }
}