package com.athletiq.backend.security.auth.service;

import com.athletiq.backend.security.auth.dto.LoginRequest;
import com.athletiq.backend.security.auth.dto.LoginResponse;
import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.auth.exception.AuthenticationFailureException;
import com.athletiq.backend.security.auth.repository.UserRepository;
import com.athletiq.backend.security.jwt.JwtService;
import com.athletiq.backend.security.session.service.RefreshSessionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshSessionService refreshSessionService;

    public LoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshSessionService refreshSessionService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshSessionService = refreshSessionService;
    }

    public LoginResponse authenticate(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new AuthenticationFailureException(
                                "Invalid email or password"
                        )
                );

        if (!user.isEnabled()) {
            throw new AuthenticationFailureException(
                    "Account is disabled"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new AuthenticationFailureException(
                    "Invalid email or password"
            );
        }

        if (!user.isEmailVerified()) {
            throw new AuthenticationFailureException(
                    "Email address is not verified"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                refreshSessionService.createRefreshToken(user);

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                accessToken,
                refreshToken,
                "Bearer"
        );
    }
}
