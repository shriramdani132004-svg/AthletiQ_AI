package com.athletiq.backend.application.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.ApplicationStatus;
import com.athletiq.backend.application.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationStateService {

    private final ApplicationRepository applicationRepository;

    public ApplicationStateService(
            ApplicationRepository applicationRepository
    ) {
        this.applicationRepository =
                applicationRepository;
    }

    @Transactional
    public ApplicationStatus transition(
            Long applicationId,
            ApplicationStatus targetStatus
    ) {

        if(applicationId == null) {

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if(targetStatus == null) {

            throw new IllegalArgumentException(
                    "Target application status is required."
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

        ApplicationStatus currentStatus =
                application.getStatus();

        if(currentStatus == null) {

            currentStatus =
                    ApplicationStatus.SUBMITTED;
        }

        if(
                currentStatus == targetStatus
        ) {

            throw new IllegalStateException(
                    "Application is already in status " +
                            currentStatus
            );
        }

        if(
                !isValidTransition(
                        currentStatus,
                        targetStatus
                )
        ) {

            throw new IllegalStateException(
                    "Invalid application lifecycle transition: " +
                            currentStatus +
                            " -> " +
                            targetStatus
            );
        }

        application.setStatus(
                targetStatus
        );

        return applicationRepository
                .save(application)
                .getStatus();
    }

    private boolean isValidTransition(
            ApplicationStatus current,
            ApplicationStatus target
    ) {

        return
                (
                        current ==
                                ApplicationStatus.SUBMITTED &&
                        target ==
                                ApplicationStatus.VALIDATED
                )
                ||
                (
                        current ==
                                ApplicationStatus.VALIDATED &&
                        target ==
                                ApplicationStatus.EVALUATION_PENDING
                );
    }
}