package com.athletiq.backend.form.service;

import com.athletiq.backend.form.entity.Form;
import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormVersionStatus;
import com.athletiq.backend.form.repository.FormFieldRepository;
import com.athletiq.backend.form.repository.FormRepository;
import com.athletiq.backend.form.repository.FormVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FormVersionService {

    private final FormService formService;
    private final FormRepository formRepository;
    private final FormVersionRepository formVersionRepository;
    private final FormFieldRepository formFieldRepository;

    public FormVersionService(
            FormService formService,
            FormRepository formRepository,
            FormVersionRepository formVersionRepository,
            FormFieldRepository formFieldRepository
    ) {
        this.formService = formService;
        this.formRepository = formRepository;
        this.formVersionRepository = formVersionRepository;
        this.formFieldRepository = formFieldRepository;
    }

    @Transactional(readOnly = true)
    public List<FormVersion> getVersions(
            Long eventId,
            Long organizerId
    ) {
        return formService.getVersionsForOrganizer(
                eventId,
                organizerId
        );
    }

    @Transactional(readOnly = true)
    public FormVersion getVersion(
            Long eventId,
            Long versionId,
            Long organizerId
    ) {
        return formService.getVersionForOrganizer(
                eventId,
                versionId,
                organizerId
        );
    }

    public FormVersion createVersion(
            Long eventId,
            Long organizerId
    ) {
        Form form = formService.getFormForOrganizer(
                eventId,
                organizerId
        );

        FormVersion latest =
                formVersionRepository
                        .findTopByFormIdOrderByVersionNumberDesc(
                                form.getId()
                        )
                        .orElse(null);

        int nextVersion =
                latest == null
                        ? 1
                        : latest.getVersionNumber() + 1;

        FormVersion version = new FormVersion();
        version.setForm(form);
        version.setVersionNumber(nextVersion);
        version.setStatus(FormVersionStatus.DRAFT);

        return formVersionRepository.save(version);
    }

    public FormVersion cloneVersion(
            Long eventId,
            Long sourceVersionId,
            Long organizerId
    ) {
        Form source =
                formService.getFormForOrganizer(
                        eventId,
                        organizerId
                );

        FormVersion sourceVersion =
                formService.getVersionForOrganizer(
                        eventId,
                        sourceVersionId,
                        organizerId
                );

        int nextVersion =
                formVersionRepository
                        .findTopByFormIdOrderByVersionNumberDesc(
                                source.getId()
                        )
                        .map(version ->
                                version.getVersionNumber() + 1
                        )
                        .orElse(1);

        FormVersion newVersion = new FormVersion();
        newVersion.setForm(source);
        newVersion.setVersionNumber(nextVersion);
        newVersion.setStatus(FormVersionStatus.DRAFT);

        FormVersion savedVersion =
                formVersionRepository.save(newVersion);

        List<FormField> sourceFields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                sourceVersion.getId()
                        );

        for(FormField sourceField : sourceFields) {

            FormField copiedField = new FormField();

            copiedField.setFormVersion(savedVersion);
            copiedField.setFieldKey(
                    sourceField.getFieldKey()
            );
            copiedField.setFieldType(
                    sourceField.getFieldType()
            );
            copiedField.setLabel(
                    sourceField.getLabel()
            );
            copiedField.setDescription(
                    sourceField.getDescription()
            );
            copiedField.setDisplayOrder(
                    sourceField.getDisplayOrder()
            );
            copiedField.setRequired(
                    sourceField.isRequired()
            );
            copiedField.setValidationConfig(
                    sourceField.getValidationConfig()
            );
            copiedField.setOptionsConfig(
                    sourceField.getOptionsConfig()
            );

            formFieldRepository.save(copiedField);
        }

        return savedVersion;
    }

    public FormVersion publishVersion(
            Long eventId,
            Long versionId,
            Long organizerId
    ) {
        Form form =
                formService.getFormForOrganizer(
                        eventId,
                        organizerId
                );

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        formService.requireEditableVersion(version);

        List<FormField> fields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                version.getId()
                        );

        if(fields.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot publish an empty form"
            );
        }

        FormVersion previousPublished = null;

        if(form.getCurrentPublishedVersionId() != null) {

            previousPublished =
                    formVersionRepository
                            .findById(
                                    form.getCurrentPublishedVersionId()
                            )
                            .orElse(null);
        }

        if(previousPublished != null &&
                !previousPublished.getId().equals(version.getId())) {

            previousPublished.setStatus(
                    FormVersionStatus.LOCKED
            );

            formVersionRepository.save(previousPublished);
        }

        version.setStatus(FormVersionStatus.PUBLISHED);
        version.setPublishedAt(LocalDateTime.now());

        FormVersion savedVersion =
                formVersionRepository.save(version);

        form.setCurrentPublishedVersionId(
                savedVersion.getId()
        );

        formRepository.save(form);

        return savedVersion;
    }

    public FormVersion lockVersion(
            Long eventId,
            Long versionId,
            Long organizerId
    ) {
        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        if(version.getStatus() == FormVersionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Draft versions must be published before locking"
            );
        }

        version.setStatus(FormVersionStatus.LOCKED);

        return formVersionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public boolean isCurrentPublishedVersion(
            Long eventId,
            Long versionId,
            Long organizerId
    ) {
        Form form =
                formService.getFormForOrganizer(
                        eventId,
                        organizerId
                );

        return versionId.equals(
                form.getCurrentPublishedVersionId()
        );
    }
}