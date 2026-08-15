package com.athletiq.backend.profile.service;

import com.athletiq.backend.profile.dto.ProfilePhotoRequest;
import com.athletiq.backend.profile.entity.Profile;
import com.athletiq.backend.profile.repository.ProfileRepository;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfilePhotoService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfilePhotoService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public String getPhoto(Long userId) {
        return profileRepository.findByUserId(userId).map(Profile::getProfilePhotoUrl).orElse(null);
    }

    public String updatePhoto(Long userId, ProfilePhotoRequest request) {
        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> createProfile(userId));
        String url = request.profilePhotoUrl();
        if (url != null && url.length() > 2048) {
            throw new IllegalArgumentException("Profile photo URL is too long");
        }
        profile.setProfilePhotoUrl(url);
        profileRepository.save(profile);
        return url;
    }

    private Profile createProfile(Long userId) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        return profile;
    }
}