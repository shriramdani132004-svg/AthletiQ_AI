package com.athletiq.backend.security.jwt;

import com.athletiq.backend.security.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(
                properties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        Instant expiration = now.plusSeconds(
                properties.getAccessTokenExpirationMinutes() * 60
        );

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
