package com.athletiq.backend.security.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.athletiq.backend.security.auth.dto.LoginRequest;
import com.athletiq.backend.security.auth.dto.LoginResponse;
import com.athletiq.backend.security.auth.entity.Role;
import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.auth.exception.AuthenticationFailureException;
import com.athletiq.backend.security.auth.repository.UserRepository;
import com.athletiq.backend.security.jwt.JwtService;
import com.athletiq.backend.security.session.service.RefreshSessionService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshSessionService refreshSessionService;

    @InjectMocks
    private LoginService loginService;

    private User verifiedOrganizer() {
        User user = new User();
        user.setEmail("organizer@athletiq.test");
        user.setFirstName("Test");
        user.setLastName("Organizer");
        user.setPasswordHash("ENCODED_PASSWORD");
        user.setRole(Role.ORGANIZER);
        user.setEmailVerified(true);
        user.setEnabled(true);
        return user;
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    @Test
    void validCredentialsAuthenticateVerifiedUser() {
        User user = verifiedOrganizer();
        LoginRequest request = loginRequest(
                " Organizer@AthletiQ.Test ",
                "StrongPassword123"
        );

        when(userRepository.findByEmailIgnoreCase(
                "organizer@athletiq.test"
        )).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "StrongPassword123",
                "ENCODED_PASSWORD"
        )).thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("test-access-token");

        when(refreshSessionService.createRefreshToken(user))
                .thenReturn("test-refresh-token");

        LoginResponse response = loginService.authenticate(request);

        assertEquals("organizer@athletiq.test", response.email());
        assertEquals(Role.ORGANIZER, response.role());
        assertEquals("Test", response.firstName());

        verify(passwordEncoder).matches(
                "StrongPassword123",
                "ENCODED_PASSWORD"
        );
        verify(jwtService).generateAccessToken(user);
        verify(refreshSessionService).createRefreshToken(user);
    }

    @Test
    void wrongPasswordIsRejected() {
        User user = verifiedOrganizer();
        LoginRequest request = loginRequest(
                "organizer@athletiq.test",
                "WrongPassword"
        );

        when(userRepository.findByEmailIgnoreCase(
                "organizer@athletiq.test"
        )).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "ENCODED_PASSWORD"
        )).thenReturn(false);

        assertThrows(
                AuthenticationFailureException.class,
                () -> loginService.authenticate(request)
        );

        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(refreshSessionService, never()).createRefreshToken(any(User.class));
    }

    @Test
    void unverifiedEmailIsRejected() {
        User user = verifiedOrganizer();
        user.setEmailVerified(false);

        LoginRequest request = loginRequest(
                "organizer@athletiq.test",
                "StrongPassword123"
        );

        when(userRepository.findByEmailIgnoreCase(
                "organizer@athletiq.test"
        )).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "StrongPassword123",
                "ENCODED_PASSWORD"
        )).thenReturn(true);

        assertThrows(
                AuthenticationFailureException.class,
                () -> loginService.authenticate(request)
        );

        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(refreshSessionService, never()).createRefreshToken(any(User.class));
    }

    @Test
    void disabledAccountIsRejected() {
        User user = verifiedOrganizer();
        user.setEnabled(false);

        LoginRequest request = loginRequest(
                "organizer@athletiq.test",
                "StrongPassword123"
        );

        when(userRepository.findByEmailIgnoreCase(
                "organizer@athletiq.test"
        )).thenReturn(Optional.of(user));

        assertThrows(
                AuthenticationFailureException.class,
                () -> loginService.authenticate(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(refreshSessionService, never()).createRefreshToken(any(User.class));
    }

    @Test
    void unknownEmailIsRejected() {
        LoginRequest request = loginRequest(
                "unknown@athletiq.test",
                "StrongPassword123"
        );

        when(userRepository.findByEmailIgnoreCase(
                "unknown@athletiq.test"
        )).thenReturn(Optional.empty());

        assertThrows(
                AuthenticationFailureException.class,
                () -> loginService.authenticate(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(refreshSessionService, never()).createRefreshToken(any(User.class));
    }
}