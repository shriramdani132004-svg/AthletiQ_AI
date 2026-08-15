package com.athletiq.backend.security.auth.controller;

import com.athletiq.backend.security.auth.dto.RegisterRequest;
import com.athletiq.backend.security.auth.dto.RegisterResponse;
import com.athletiq.backend.security.auth.dto.LoginRequest;
import com.athletiq.backend.security.auth.dto.LoginResponse;
import com.athletiq.backend.security.auth.service.RegistrationService;
import com.athletiq.backend.security.auth.service.LoginService;
import com.athletiq.backend.security.session.service.RefreshSessionService;
import com.athletiq.backend.security.session.dto.RefreshTokenRequest;
import com.athletiq.backend.security.session.dto.RefreshTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final RefreshSessionService refreshSessionService;
    public AuthController(RegistrationService registrationService, LoginService loginService, RefreshSessionService refreshSessionService) {
        this.registrationService = registrationService;
        this.loginService = loginService;
        this.refreshSessionService = refreshSessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.registerOrganizer(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                loginService.authenticate(request)
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        String accessToken =
                refreshSessionService.rotateAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(
                new RefreshTokenResponse(accessToken, "Bearer")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        refreshSessionService.revoke(
                request.getRefreshToken()
        );

        return ResponseEntity.noContent().build();
    }

}
