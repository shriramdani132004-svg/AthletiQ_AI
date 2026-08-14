package com.athletiq.backend.profile.service;

import com.athletiq.backend.profile.dto.OrganizationInfoRequest;
import com.athletiq.backend.profile.dto.OrganizationInfoResponse;
import com.athletiq.backend.profile.entity.Profile;
import com.athletiq.backend.profile.repository.ProfileRepository;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class OrganizationInfoService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public OrganizationInfoService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public OrganizationInfoResponse getOrganizationInfo(String email) {
        Long userId = findUserId(email);
        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> createProfile(userId));
        return new OrganizationInfoResponse(profile.getUserId(), profile.getOrganizationName(), profile.getOrganizationDescription());
    }

    public OrganizationInfoResponse updateOrganizationInfo(String email, OrganizationInfoRequest request) {
        Long userId = findUserId(email);
        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> createProfile(userId));
        profile.setOrganizationName(request.organizationName());
        profile.setOrganizationDescription(request.organizationDescription());
        Profile saved = profileRepository.save(profile);
        return new OrganizationInfoResponse(saved.getUserId(), saved.getOrganizationName(), saved.getOrganizationDescription());
    }

    private Long findUserId(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Authenticated user not found")).getId();
    }

    private Profile createProfile(Long userId) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        return profile;
    }
}