package com.athletiq.backend.event.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank String name,
        @NotBlank String sport,
        String description,
        String location,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull @FutureOrPresent LocalDate endDate,
        @NotNull @FutureOrPresent LocalDateTime registrationDeadline,
        @NotNull @Min(1) Integer playersRequired,
        String ageCategory,
        String eligibilityCriteria,
        String eventRules,
        String bannerUrl
) {}