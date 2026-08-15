package com.athletiq.backend.application.service;

import com.athletiq.backend.application.dto.ApplicationResponse;
import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.form.entity.Form;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormVersionStatus;
import com.athletiq.backend.form.repository.FormVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FormVersionRepository formVersionRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            FormVersionRepository formVersionRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.formVersionRepository = formVersionRepository;
    }

    @Transactional
    public ApplicationResponse createApplication(
            Event event,
            Long formVersionId,
            Long applicantId
    ) {
        if (event == null || event.getId() == null) {
            throw new IllegalArgumentException("Event is required.");
        }

        if (formVersionId == null) {
            throw new IllegalArgumentException("Form version is required.");
        }

        if (applicantId == null) {
            throw new IllegalArgumentException("Applicant is required.");
        }

        FormVersion formVersion =
                formVersionRepository.findById(formVersionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Form version not found."
                                )
                        );

        Form form = formVersion.getForm();

        if (form == null || form.getEvent() == null) {
            throw new IllegalArgumentException(
                    "Form version is not linked to an event."
            );
        }

        if (!event.getId().equals(form.getEvent().getId())) {
            throw new IllegalArgumentException(
                    "Form version does not belong to the requested event."
            );
        }

        if (formVersion.getStatus() != FormVersionStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Applications are allowed only against a PUBLISHED form version."
            );
        }

        if (form.getCurrentPublishedVersionId() == null ||
                !form.getCurrentPublishedVersionId().equals(
                        formVersion.getId()
                )) {
            throw new IllegalStateException(
                    "Applications are allowed only against the current published form version."
            );
        }

        Application application = new Application();
        application.setEvent(event);
        application.setFormVersion(formVersion);
        application.setApplicantId(applicantId);
        application.setSubmittedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        return new ApplicationResponse(
                saved.getId(),
                event.getId(),
                formVersion.getId(),
                saved.getApplicantId(),
                saved.getSubmittedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<Application> getApplicationsByEvent(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID is required.");
        }

        return applicationRepository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<Application> getApplicationsByFormVersion(Long formVersionId) {
        if (formVersionId == null) {
            throw new IllegalArgumentException("Form version ID is required.");
        }

        return applicationRepository.findByFormVersionId(formVersionId);
    }

    @Transactional(readOnly = true)
    public List<Application> getApplicationsByApplicant(Long applicantId) {
        if (applicantId == null) {
            throw new IllegalArgumentException("Applicant ID is required.");
        }

        return applicationRepository.findByApplicantId(applicantId);
    }
}