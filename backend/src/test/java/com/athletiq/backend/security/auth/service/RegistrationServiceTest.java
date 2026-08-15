package com.athletiq.backend.security.auth.service;

import com.athletiq.backend.security.auth.dto.RegisterRequest;
import com.athletiq.backend.security.auth.dto.RegisterResponse;
import com.athletiq.backend.security.auth.entity.Role;
import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.verification.EmailVerificationService;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void organizerRegistrationCreatesOrganizerWithEncodedPassword() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("  Organizer@AthletiQ.Test ");
        request.setPassword("StrongPassword123");
        request.setFirstName(" Test ");
        request.setLastName(" Organizer ");

        when(userRepository.existsByEmailIgnoreCase("organizer@athletiq.test"))
                .thenReturn(false);

        when(passwordEncoder.encode("StrongPassword123"))
                .thenReturn("ENCODED_PASSWORD");

        User saved = new User();
        saved.setEmail("organizer@athletiq.test");
        saved.setFirstName("Test");
        saved.setLastName("Organizer");
        saved.setPasswordHash("ENCODED_PASSWORD");
        saved.setRole(Role.ORGANIZER);
        saved.setEmailVerified(false);
        saved.setEnabled(true);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        doNothing().when(emailVerificationService).sendVerification(anyString(), eq("organizer@athletiq.test"));

        RegisterResponse response =
                registrationService.registerOrganizer(request);

        assertEquals("organizer@athletiq.test", response.email());
        assertEquals("Test", response.firstName());
        assertEquals("Organizer", response.lastName());
        assertEquals(Role.ORGANIZER, response.role());
        assertFalse(response.emailVerified());

        verify(passwordEncoder).encode("StrongPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void duplicateEmailIsRejected() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("organizer@athletiq.test");
        request.setPassword("StrongPassword123");
        request.setFirstName("Test");
        request.setLastName("Organizer");

        when(userRepository.existsByEmailIgnoreCase("organizer@athletiq.test"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> registrationService.registerOrganizer(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
