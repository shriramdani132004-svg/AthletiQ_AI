package com.athletiq.backend.application.dto;

import com.athletiq.backend.application.entity.SelectionStatus;

public record SelectionDecisionRequest(
        SelectionStatus selectionStatus,
        String selectionReason
) {
}
