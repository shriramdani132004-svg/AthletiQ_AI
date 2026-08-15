package com.athletiq.backend.profile.service;

import com.athletiq.backend.profile.dto.ChangePasswordRequest;
import com.athletiq.backend.profile.dto.ProfileResponse;
import com.athletiq.backend.profile.dto.UpdateProfileRequest;
import com.athletiq.backend.profile.entity.Profile;
import com.athletiq.backend.profile.repository.ProfileRepository;
import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse getProfileByUserId(Long userId) {
        return getProfile(userId);
    }

    public ProfileResponse updateProfileByUserId(Long userId, UpdateProfileRequest request) {
        return updateProfile(userId, request);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (request.newPassword() == null || request.newPassword().length() < 8) {
            throw new IllegalArgumentException("New password must contain at least 8 characters");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public ProfileResponse getProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> createEmptyProfile(userId));
        return toResponse(profile);
    }

    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> createEmptyProfile(userId));
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setProfilePhotoUrl(request.profilePhotoUrl());
        profile.setOrganizationName(request.organizationName());
        profile.setOrganizationDescription(request.organizationDescription());
        return toResponse(profileRepository.save(profile));
    }

    private Profile createEmptyProfile(Long userId) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        return profile;
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(profile.getUserId(), profile.getFirstName(), profile.getLastName(), profile.getPhoneNumber(), profile.getProfilePhotoUrl(), profile.getOrganizationName(), profile.getOrganizationDescription());
    }
}