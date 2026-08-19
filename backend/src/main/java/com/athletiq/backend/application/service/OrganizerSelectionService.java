package com.athletiq.backend.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.athletiq.backend.application.dto.SelectionDecisionRequest;
import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.SelectionStatus;
import com.athletiq.backend.application.repository.ApplicationRepository;

@Service
public class OrganizerSelectionService {
    private final SelectionEmailService selectionEmailService;
    private final ApplicationRepository applicationRepository;

   public OrganizerSelectionService(
        ApplicationRepository applicationRepository,
        SelectionEmailService selectionEmailService
) {
    this.applicationRepository =
            applicationRepository;

    this.selectionEmailService =
            selectionEmailService;
}

    @Transactional
    public Map<String, Object> decide(
            Long organizerId,
            Long applicationId,
            SelectionDecisionRequest request
    ) {
        if (organizerId == null) {
            throw new IllegalArgumentException(
                    "Organizer ID is required."
            );
        }

        if (applicationId == null) {
            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if (request == null ||
                request.selectionStatus() == null) {
            throw new IllegalArgumentException(
                    "Selection status is required."
            );
        }

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Application not found."
                                )
                        );

        if (application.getEvent() == null ||
                application.getEvent().getOrganizerId() == null ||
                !organizerId.equals(
                        application.getEvent().getOrganizerId()
                )) {
            throw new IllegalArgumentException(
                    "Application does not belong to this organizer."
            );
        }

        SelectionStatus target =
                request.selectionStatus();

        if (target == SelectionStatus.NOT_REVIEWED) {
            application.setSelectionReason(null);
            application.setSelectionDecidedAt(null);
        } else {
            application.setSelectionReason(
                    normalizeReason(
                            request.selectionReason()
                    )
            );
            application.setSelectionDecidedAt(
                    LocalDateTime.now()
            );
        }

        application.setSelectionStatus(target);

        if (target == SelectionStatus.SELECTED) {
    selectionEmailService.sendSelectionEmail(
            application
    );
}

        Application saved =
                applicationRepository.save(application);

        return Map.of(
                "applicationId",
                saved.getId(),
                "selectionStatus",
                saved.getSelectionStatus().name(),
                "selectionReason",
                saved.getSelectionReason() == null
                        ? ""
                        : saved.getSelectionReason(),
                "selectionDecidedAt",
                saved.getSelectionDecidedAt() == null
                        ? ""
                        : saved.getSelectionDecidedAt().toString()
        );
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }

        String normalized =
                reason.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > 2000) {
            throw new IllegalArgumentException(
                    "Selection reason must not exceed 2000 characters."
            );
        }

        return normalized;
    }
}