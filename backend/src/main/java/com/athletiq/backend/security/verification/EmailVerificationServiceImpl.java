package com.athletiq.backend.security.verification;

import com.athletiq.backend.security.auth.entity.User;
import com.athletiq.backend.security.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final long TOKEN_TTL_SECONDS = 24 * 60 * 60;

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, VerificationToken> tokens = new ConcurrentHashMap<>();

    public EmailVerificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void sendVerification(String userId, String email) {
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes());

        VerificationToken verificationToken = new VerificationToken(
                token,
                email,
                Instant.now().plusSeconds(TOKEN_TTL_SECONDS)
        );

        tokens.put(token, verificationToken);

        System.out.println(
                "[DEV EMAIL VERIFICATION] email=" +
                email +
                " token=" +
                token
        );
    }

    @Override
    @Transactional
    public boolean verify(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        VerificationToken verificationToken = tokens.get(token);

        if (verificationToken == null ||
                verificationToken.isExpired() ||
                verificationToken.getStatus() != VerificationStatus.PENDING) {
            return false;
        }

        User user = userRepository
                .findByEmailIgnoreCase(verificationToken.getEmail())
                .orElse(null);

        if (user == null) {
            return false;
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setStatus(VerificationStatus.VERIFIED);
        tokens.put(token, verificationToken);

        return true;
    }

    @Override
    public void sendResendVerification(String userId, String email) {
        sendVerification(userId, email);
    }

    private byte[] randomBytes() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}