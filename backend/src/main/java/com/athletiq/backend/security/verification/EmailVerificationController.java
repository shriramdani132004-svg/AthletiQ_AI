package com.athletiq.backend.security.verification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(
            EmailVerificationService emailVerificationService
    ) {
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verify(
            @RequestBody VerificationTokenRequest request
    ) {
        boolean verified =
                emailVerificationService.verify(request.token());

        if (!verified) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new VerificationResponse(
                                    "FAILED",
                                    "Invalid or expired verification token"
                            )
                    );
        }

        return ResponseEntity.ok(
                new VerificationResponse(
                        "VERIFIED",
                        "Email address verified successfully"
                )
        );
    }
}