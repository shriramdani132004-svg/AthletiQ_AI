package com.athletiq.backend.profile.controller;

import com.athletiq.backend.profile.dto.ProfileResponse;
import com.athletiq.backend.profile.dto.ChangePasswordRequest;
import com.athletiq.backend.profile.dto.UpdateProfileRequest;
import com.athletiq.backend.profile.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZER')")
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(Authentication authentication) {
        return profileService.getProfileByUserId(Long.valueOf(authentication.getName()));
    }

    @PutMapping("/password")
    public void changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request
    ) {
        profileService.changePassword(Long.valueOf(authentication.getName()), request);
    }

    @PutMapping
    public ProfileResponse updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfileByUserId(Long.valueOf(authentication.getName()), request);
    }
}