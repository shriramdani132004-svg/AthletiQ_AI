package com.athletiq.backend.security.config;

import com.athletiq.backend.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityFilterConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityFilterConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/health",
                    "/actuator/health",
                    "/api/public/**"
                ).permitAll()

                .requestMatchers("/api/v1/admin/**")
                    .hasRole("SUPER_ADMIN")

                .requestMatchers("/api/v1/organizer/**")
                    .hasAnyRole(
                        "SUPER_ADMIN",
                        "ORGANIZER"
                    )

                .requestMatchers("/api/v1/staff/**")
                    .hasAnyRole(
                        "SUPER_ADMIN",
                        "ORGANIZER",
                        "STAFF"
                    )

                .requestMatchers("/api/v1/player/**")
                    .hasAnyRole(
                        "SUPER_ADMIN",
                        "ORGANIZER",
                        "STAFF",
                        "PLAYER"
                    )

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}