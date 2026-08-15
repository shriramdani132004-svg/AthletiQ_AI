package com.athletiq.backend.application.service;

import com.athletiq.backend.application.dto.OrganizerApplicationResponse;
import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizerApplicationService {

    private final EventRepository eventRepository;
    private final ApplicationRepository applicationRepository;

    public OrganizerApplicationService(
            EventRepository eventRepository,
            ApplicationRepository applicationRepository
    ) {
        this.eventRepository = eventRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizerApplicationResponse> getApplications(
            Long organizerId,
            Long eventId
    ) {

        if (organizerId == null) {
            throw new IllegalArgumentException(
                    "Organizer is required."
            );
        }

        Event event =
                eventRepository
                        .findByIdAndOrganizerId(
                                eventId,
                                organizerId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Event not found or access denied."
                                )
                        );

        return applicationRepository
                .findByEventId(event.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrganizerApplicationResponse toResponse(
            Application application
    ) {

        Integer versionNumber =
                application.getFormVersion() == null
                        ? null
                        : application
                            .getFormVersion()
                            .getVersionNumber();

        return new OrganizerApplicationResponse(
                application.getId(),
                application.getEvent().getId(),
                application.getFormVersion().getId(),
                versionNumber,
                application.getApplicantId(),
                application.getSubmittedData(),
                application.getSubmittedAt()
        );
    }
}