package com.athletiq.backend.profile.controller;

import com.athletiq.backend.profile.dto.EmailPreferencesRequest;
import com.athletiq.backend.profile.dto.EmailPreferencesResponse;
import com.athletiq.backend.profile.service.EmailPreferencesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZER')")
@RequestMapping("/api/v1/profile/email-preferences")
public class EmailPreferencesController {

    private final EmailPreferencesService emailPreferencesService;

    public EmailPreferencesController(EmailPreferencesService emailPreferencesService) {
        this.emailPreferencesService = emailPreferencesService;
    }

    @GetMapping
    public EmailPreferencesResponse getPreferences(Authentication authentication) {
        return emailPreferencesService.getPreferences(authentication.getName());
    }

    @PutMapping
    public EmailPreferencesResponse updatePreferences(Authentication authentication, @RequestBody EmailPreferencesRequest request) {
        return emailPreferencesService.updatePreferences(authentication.getName(), request);
    }
}