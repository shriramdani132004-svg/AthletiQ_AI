package com.athletiq.backend.objectiveevaluation.dto;

import java.util.List;

public record CriterionValueExtractionResult(

        Long applicationId,

        Long eventId,

        List<CriterionValueExtraction> criteria,

        List<String> unmappedCriteria

) {
}