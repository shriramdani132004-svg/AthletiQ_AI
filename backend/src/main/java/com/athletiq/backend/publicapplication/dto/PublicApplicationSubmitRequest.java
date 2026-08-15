package com.athletiq.backend.publicapplication.dto;

import java.util.Map;

public record PublicApplicationSubmitRequest(
        Map<String, Object> answers
) {
}