package com.athletiq.backend.objectiveevaluation.dto;

import java.util.List;
import java.util.Map;

public record NormalizedApplicationAnswers(

        Long applicationId,

        Long formVersionId,

        Map<String, NormalizedAnswer> answers,

        List<String> missingFieldKeys

) {
}