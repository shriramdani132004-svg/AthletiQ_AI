package com.athletiq.backend.profile.controller;

import com.athletiq.backend.profile.dto.ProfilePhotoRequest;
import com.athletiq.backend.profile.service.ProfilePhotoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZER')")
@RequestMapping("/api/v1/profile/photo")
public class ProfilePhotoController {

    private final ProfilePhotoService profilePhotoService;

    public ProfilePhotoController(ProfilePhotoService profilePhotoService) {
        this.profilePhotoService = profilePhotoService;
    }

    @GetMapping
    public String getPhoto(Authentication authentication) {
        return profilePhotoService.getPhoto(authentication.getName());
    }

    @PutMapping
    public String updatePhoto(Authentication authentication, @RequestBody ProfilePhotoRequest request) {
        return profilePhotoService.updatePhoto(authentication.getName(), request);
    }
}