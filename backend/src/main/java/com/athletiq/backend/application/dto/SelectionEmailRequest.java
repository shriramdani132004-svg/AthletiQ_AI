package com.athletiq.backend.application.dto;

public record SelectionEmailRequest(
        String subject,
        String message
) {
}