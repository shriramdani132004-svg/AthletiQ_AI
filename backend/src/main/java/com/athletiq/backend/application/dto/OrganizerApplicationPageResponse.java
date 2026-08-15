package com.athletiq.backend.application.dto;

import java.util.List;

public record OrganizerApplicationPageResponse(

        List<OrganizerApplicationSummaryResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean first,

        boolean last

) {
}