package com.athletiq.backend.publicapplication.controller;

import com.athletiq.backend.publicapplication.dto.PublicApplicationResponse;
import com.athletiq.backend.publicapplication.dto.PublicApplicationSubmitRequest;
import com.athletiq.backend.publicapplication.dto.PublicApplicationSubmitResponse;
import com.athletiq.backend.publicapplication.service.PublicApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/apply")
public class PublicApplicationController {

    private final PublicApplicationService service;

    public PublicApplicationController(
            PublicApplicationService service
    ) {
        this.service = service;
    }

    @GetMapping("/{publicCode}")
    public ResponseEntity<PublicApplicationResponse> getPublicApplication(
            @PathVariable String publicCode
    ) {
        return ResponseEntity.ok(
                service.getPublicApplication(publicCode)
        );
    }

    @PostMapping("/{publicCode}")
    public ResponseEntity<PublicApplicationSubmitResponse>
    submitPublicApplication(
            @PathVariable String publicCode,
            @RequestBody PublicApplicationSubmitRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.submitPublicApplication(
                                publicCode,
                                request
                        )
                );
    }
}