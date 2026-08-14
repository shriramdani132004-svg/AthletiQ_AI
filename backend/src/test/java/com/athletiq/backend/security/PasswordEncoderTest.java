package com.athletiq.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    void passwordIsEncodedAndMatches() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String raw = "AthletiQ-Test-Password";
        String encoded = encoder.encode(raw);

        assertNotEquals(raw, encoded);
        assertTrue(encoder.matches(raw, encoded));
        assertFalse(encoder.matches("wrong-password", encoded));
    }
}
