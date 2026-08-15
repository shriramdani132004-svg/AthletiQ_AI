package com.athletiq.backend.publicapplication.dto;

public record PublicFormFieldResponse(
        Long id,
        String fieldKey,
        String fieldType,
        String label,
        String description,
        Integer displayOrder,
        boolean required,
        String validationConfig,
        String optionsConfig
) {
}