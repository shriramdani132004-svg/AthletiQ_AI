package com.athletiq.backend.form.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import com.athletiq.backend.form.entity.Form;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormVersionStatus;
import com.athletiq.backend.form.repository.FormRepository;
import com.athletiq.backend.form.repository.FormVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FormService {

    private final FormRepository formRepository;
    private final FormVersionRepository formVersionRepository;
    private final EventRepository eventRepository;

    public FormService(
            FormRepository formRepository,
            FormVersionRepository formVersionRepository,
            EventRepository eventRepository
    ) {
        this.formRepository = formRepository;
        this.formVersionRepository = formVersionRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public Form getFormForOrganizer(
            Long eventId,
            Long organizerId
    ) {

        requireEventOwnership(eventId, organizerId);

        return formRepository.findByEventId(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Form does not exist for this event"
                        )
                );
    }

    public Form createForm(
            Long eventId,
            Long organizerId
    ) {

        Event event = requireEventOwnership(
                eventId,
                organizerId
        );

        if (formRepository.existsByEventId(eventId)) {
            throw new IllegalStateException(
                    "A form already exists for this event"
            );
        }

        Form form = new Form();
        form.setEvent(event);

        return formRepository.save(form);
    }

    public FormVersion createDraftVersion(
            Long eventId,
            Long organizerId
    ) {

        Form form = getFormForOrganizer(
                eventId,
                organizerId
        );

        FormVersion latestVersion =
                formVersionRepository
                        .findTopByFormIdOrderByVersionNumberDesc(
                                form.getId()
                        )
                        .orElse(null);

        int nextVersion =
                latestVersion == null
                        ? 1
                        : latestVersion.getVersionNumber() + 1;

        FormVersion version = new FormVersion();
        version.setForm(form);
        version.setVersionNumber(nextVersion);
        version.setStatus(FormVersionStatus.DRAFT);

        return formVersionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public List<FormVersion> getVersionsForOrganizer(
            Long eventId,
            Long organizerId
    ) {

        Form form = getFormForOrganizer(
                eventId,
                organizerId
        );

        return formVersionRepository
                .findByFormIdOrderByVersionNumberDesc(
                        form.getId()
                );
    }

    @Transactional(readOnly = true)
    public FormVersion getVersionForOrganizer(
            Long eventId,
            Long versionId,
            Long organizerId
    ) {

        Form form = getFormForOrganizer(
                eventId,
                organizerId
        );

        FormVersion version =
                formVersionRepository.findById(versionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Form version not found"
                                )
                        );

        if (!version.getForm().getId().equals(form.getId())) {
            throw new IllegalArgumentException(
                    "Form version does not belong to this event"
            );
        }

        return version;
    }

    public void requireEditableVersion(
            FormVersion version
    ) {

        if (version.getStatus() != FormVersionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft form versions can be modified"
            );
        }
    }

    private Event requireEventOwnership(
            Long eventId,
            Long organizerId
    ) {

        return eventRepository.findByIdAndOrganizerId(
                        eventId,
                        organizerId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found or access denied"
                        )
                );
    }
}