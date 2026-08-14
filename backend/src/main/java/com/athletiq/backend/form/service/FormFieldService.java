package com.athletiq.backend.form.service;

import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.repository.FormFieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FormFieldService {

    private final FormFieldRepository formFieldRepository;
    private final FormService formService;
    private final FormValidationService formValidationService;

    public FormFieldService(
            FormFieldRepository formFieldRepository,
            FormService formService,
            FormValidationService formValidationService
    ) {
        this.formFieldRepository = formFieldRepository;
        this.formService = formService;
        this.formValidationService = formValidationService;
    }

    @Transactional(readOnly = true)
    public List<FormField> getFields(
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

        return formFieldRepository
                .findByFormVersionIdOrderByDisplayOrderAsc(
                        version.getId()
                );
    }

    @Transactional(readOnly = true)
    public FormField getField(
            Long eventId,
            Long versionId,
            Long fieldId,
            Long organizerId
    ) {

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        FormField field =
                formFieldRepository.findById(fieldId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Form field not found"
                                )
                        );

        if (!field.getFormVersion()
                .getId()
                .equals(version.getId())) {

            throw new IllegalArgumentException(
                    "Form field does not belong to this form version"
            );
        }

        return field;
    }

    public FormField addField(
            Long eventId,
            Long versionId,
            Long organizerId,
            FormField field
    ) {

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        formService.requireEditableVersion(version);

        formValidationService.validateField(field);

        if (formFieldRepository
                .existsByFormVersionIdAndFieldKey(
                        version.getId(),
                        field.getFieldKey()
                )) {

            throw new IllegalArgumentException(
                    "Field key already exists in this form version"
            );
        }

        if (formFieldRepository
                .existsByFormVersionIdAndDisplayOrder(
                        version.getId(),
                        field.getDisplayOrder()
                )) {

            throw new IllegalArgumentException(
                    "Display order already exists in this form version"
            );
        }

        field.setFormVersion(version);

        return formFieldRepository.save(field);
    }

    public FormField updateField(
            Long eventId,
            Long versionId,
            Long fieldId,
            Long organizerId,
            FormField request
    ) {

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        formService.requireEditableVersion(version);

        FormField existing =
                getField(
                        eventId,
                        versionId,
                        fieldId,
                        organizerId
                );

        formValidationService.validateField(request);

        if (!existing.getFieldKey()
                .equals(request.getFieldKey())
                &&
                formFieldRepository
                        .existsByFormVersionIdAndFieldKey(
                                version.getId(),
                                request.getFieldKey()
                        )) {

            throw new IllegalArgumentException(
                    "Field key already exists in this form version"
            );
        }

        if (!existing.getDisplayOrder()
                .equals(request.getDisplayOrder())
                &&
                formFieldRepository
                        .existsByFormVersionIdAndDisplayOrder(
                                version.getId(),
                                request.getDisplayOrder()
                        )) {

            throw new IllegalArgumentException(
                    "Display order already exists in this form version"
            );
        }

        existing.setFieldKey(
                request.getFieldKey()
        );

        existing.setFieldType(
                request.getFieldType()
        );

        existing.setLabel(
                request.getLabel()
        );

        existing.setDescription(
                request.getDescription()
        );

        existing.setDisplayOrder(
                request.getDisplayOrder()
        );

        existing.setRequired(
                request.isRequired()
        );

        existing.setValidationConfig(
                request.getValidationConfig()
        );

        existing.setOptionsConfig(
                request.getOptionsConfig()
        );

        return formFieldRepository.save(existing);
    }

    public void deleteField(
            Long eventId,
            Long versionId,
            Long fieldId,
            Long organizerId
    ) {

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        formService.requireEditableVersion(version);

        FormField field =
                getField(
                        eventId,
                        versionId,
                        fieldId,
                        organizerId
                );

        formFieldRepository.delete(field);
    }

    public FormField duplicateField(
            Long eventId,
            Long versionId,
            Long fieldId,
            Long organizerId
    ) {

        FormVersion version =
                formService.getVersionForOrganizer(
                        eventId,
                        versionId,
                        organizerId
                );

        formService.requireEditableVersion(version);

        FormField source =
                getField(
                        eventId,
                        versionId,
                        fieldId,
                        organizerId
                );

        List<FormField> fields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                version.getId()
                        );

        int nextOrder = fields.size();

        String baseKey =
                source.getFieldKey() + "_copy";

        String newKey = baseKey;

        int suffix = 1;

        while (formFieldRepository
                .existsByFormVersionIdAndFieldKey(
                        version.getId(),
                        newKey
                )) {

            newKey =
                    baseKey + "_" + suffix;

            suffix++;
        }

        FormField copy = new FormField();

        copy.setFormVersion(version);

        copy.setFieldKey(newKey);

        copy.setFieldType(
                source.getFieldType()
        );

        copy.setLabel(
                source.getLabel() + " Copy"
        );

        copy.setDescription(
                source.getDescription()
        );

        copy.setDisplayOrder(
                nextOrder
        );

        copy.setRequired(
                source.isRequired()
        );

        copy.setValidationConfig(
                source.getValidationConfig()
        );

        copy.setOptionsConfig(
                source.getOptionsConfig()
        );

        formValidationService.validateField(copy);

        return formFieldRepository.save(copy);
    }
}