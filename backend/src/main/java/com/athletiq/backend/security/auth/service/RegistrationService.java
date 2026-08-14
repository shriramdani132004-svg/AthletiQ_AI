package com.athletiq.backend.security.auth.service;

import com.athletiq.backend.security.auth.dto.RegisterRequest;
import com.athletiq.backend.security.auth.dto.RegisterResponse;
import com.athletiq.backend.security.auth.entity.Role;
import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse registerOrganizer(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());

        // Public registration can create ORGANIZER accounts only.
        // Privileged roles must never be supplied by the client.
        user.setRole(Role.ORGANIZER);

        user.setEmailVerified(false);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return RegisterResponse.from(savedUser);
    }
}
