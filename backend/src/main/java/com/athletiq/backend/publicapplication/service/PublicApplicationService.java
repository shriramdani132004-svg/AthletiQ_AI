package com.athletiq.backend.publicapplication.service;
import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.repository.ApplicationRepository;
import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormVersionStatus;
import com.athletiq.backend.form.repository.FormFieldRepository;
import com.athletiq.backend.form.repository.FormVersionRepository;
import com.athletiq.backend.publicapplication.dto.PublicApplicationResponse;
import com.athletiq.backend.publicapplication.dto.PublicApplicationSubmitRequest;
import com.athletiq.backend.publicapplication.dto.PublicApplicationSubmitResponse;
import com.athletiq.backend.publicapplication.dto.PublicFormFieldResponse;
import com.athletiq.backend.publicapplication.entity.PublicApplicationLink;
import com.athletiq.backend.publicapplication.repository.PublicApplicationLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicApplicationService {

    private final PublicApplicationLinkRepository linkRepository;
    private final FormVersionRepository formVersionRepository;
    private final FormFieldRepository formFieldRepository;
    private final ApplicationRepository applicationRepository;

    public PublicApplicationService(
            PublicApplicationLinkRepository linkRepository,
            FormVersionRepository formVersionRepository,
            FormFieldRepository formFieldRepository,
            ApplicationRepository applicationRepository
    ) {
        this.linkRepository = linkRepository;
        this.formVersionRepository = formVersionRepository;
        this.formFieldRepository = formFieldRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public PublicApplicationResponse getPublicApplication(
            String publicCode
    ) {
        PublicApplicationLink link = resolvePublishedLink(publicCode);

        FormVersion formVersion = resolvePublishedVersion(link);

        List<FormField> fields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                formVersion.getId()
                        );

        List<PublicFormFieldResponse> fieldResponses =
                fields.stream()
                        .map(this::toFieldResponse)
                        .toList();

        var event = link.getEvent();

        return new PublicApplicationResponse(
                event.getId(),
                event.getName(),
                event.getSport(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDate(),
                event.getEndDate(),
                event.getRegistrationDeadline(),
                event.getPlayersRequired(),
                event.getAgeCategory(),
                event.getEligibilityCriteria(),
                event.getEventRules(),
                formVersion.getId(),
                formVersion.getVersionNumber(),
                fieldResponses
        );
    }

    @Transactional
    public PublicApplicationSubmitResponse submitPublicApplication(
            String publicCode,
            PublicApplicationSubmitRequest request
    ) {
        PublicApplicationLink link = resolvePublishedLink(publicCode);

        FormVersion formVersion = resolvePublishedVersion(link);

        Map<String, Object> answers =
                request == null || request.answers() == null
                        ? Map.of()
                        : new HashMap<>(request.answers());

        List<FormField> fields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                formVersion.getId()
                        );

        validateAnswers(fields, answers);

        final String submittedData =
                serializeAnswers(answers);

        Application application = new Application();

        application.setEvent(link.getEvent());
        application.setFormVersion(formVersion);
        application.setApplicantId(null);
        application.setSubmittedData(submittedData);
        application.setSubmittedAt(LocalDateTime.now());

        Application saved =
                applicationRepository.save(application);

        return new PublicApplicationSubmitResponse(
                saved.getId(),
                link.getEvent().getId(),
                formVersion.getId(),
                saved.getSubmittedAt()
        );
    }

    private PublicApplicationLink resolvePublishedLink(
            String publicCode
    ) {
        if (publicCode == null || publicCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Public application code is required."
            );
        }

        PublicApplicationLink link =
                linkRepository.findByPublicCode(publicCode)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Public application link not found."
                                )
                        );

        if (!link.isActive()) {
            throw new IllegalStateException(
                    "This public application link is inactive."
            );
        }

        return link;
    }

    private FormVersion resolvePublishedVersion(
            PublicApplicationLink link
    ) {
        Long versionId =
                link.getFormVersion().getId();

        FormVersion formVersion =
                formVersionRepository.findById(versionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Published form version was not found."
                                )
                        );

        if (formVersion.getStatus()
                != FormVersionStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "The application form is not currently published."
            );
        }

        return formVersion;
    }

    private String serializeAnswers(
            Map<String, Object> answers
    ) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;

        for(Map.Entry<String, Object> entry : answers.entrySet()) {

            if(!first){
                json.append(",");
            }

            first = false;

            json.append("\"")
                .append(escapeJson(entry.getKey()))
                .append("\":");

            Object value = entry.getValue();

            if(value == null){
                json.append("null");
            }else if(value instanceof Number ||
                    value instanceof Boolean){
                json.append(value);
            }else{
                json.append("\"")
                    .append(
                        escapeJson(
                            String.valueOf(value)
                        )
                    )
                    .append("\"");
            }
        }

        json.append("}");

        return json.toString();
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
    private void validateAnswers(
            List<FormField> fields,
            Map<String, Object> answers
    ) {
        for (FormField field : fields) {

            Object value =
                    answers.get(field.getFieldKey());

            if (field.isRequired() &&
                    isBlank(value)) {

                throw new IllegalArgumentException(
                        "Required field missing: " +
                                field.getLabel()
                );
            }

            if (value == null ||
                    isBlank(value)) {
                continue;
            }

            String fieldType =
                    field.getFieldType() == null
                            ? ""
                            : field.getFieldType()
                                    .name();

            if ("EMAIL".equals(fieldType)) {
                String email =
                        String.valueOf(value).trim();

                if (!email.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )) {
                    throw new IllegalArgumentException(
                            "Invalid email for field: " +
                                    field.getLabel()
                    );
                }
            }
        }
    }

    private boolean isBlank(Object value) {
        return value == null ||
                (
                        value instanceof String &&
                                ((String) value).trim().isEmpty()
                );
    }

    private PublicFormFieldResponse toFieldResponse(
            FormField field
    ) {
        return new PublicFormFieldResponse(
                field.getId(),
                field.getFieldKey(),
                field.getFieldType() == null
                        ? null
                        : field.getFieldType().name(),
                field.getLabel(),
                field.getDescription(),
                field.getDisplayOrder(),
                field.isRequired(),
                field.getValidationConfig(),
                field.getOptionsConfig()
        );
    }
}