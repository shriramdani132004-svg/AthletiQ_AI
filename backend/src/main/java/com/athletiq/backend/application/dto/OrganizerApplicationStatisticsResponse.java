package com.athletiq.backend.application.dto;

public record OrganizerApplicationStatisticsResponse(

        long totalApplications,

        long pendingEvaluation,

        long evaluated,

        long selected,

        long accepted,

        long declined

) {
}