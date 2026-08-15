package com.athletiq.backend.publicapplication.service;

import com.athletiq.backend.publicapplication.exception.DuplicateApplicationException;
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
        String normalizedEmail =
                normalizeEmail(
                        answers.get("email")
                );

        String normalizedPhone =
                normalizePhone(
                        answers.get("phone")
                );

        checkDuplicateApplication(
                link.getEvent().getId(),
                null,
                normalizedEmail,
                normalizedPhone
        );

        Application application = new Application();

        application.setEvent(link.getEvent());
        application.setFormVersion(formVersion);
        application.setApplicantId(null);

        application.setApplicantName(
                stringAnswer(
                        answers,
                        "name"
                )
        );

        application.setApplicantEmail(
        normalizedEmail
);

        application.setApplicantPhone(
        normalizedPhone
);

application.setDuplicateEmail(
        normalizedEmail
);

application.setDuplicatePhone(
        normalizedPhone
);

        application.setSubmittedData(
                submittedData
        );

        application.setFileMetadata(
                extractFileMetadata(fields, answers)
        );
        application.setStatus(
                com.athletiq.backend.application.entity.ApplicationStatus.SUBMITTED
        );
        application.setSubmittedAt(
                LocalDateTime.now()
        );

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

    private void appendJsonValue(
            StringBuilder json,
            Object value
    ) {

        if(value == null){

            json.append("null");
            return;
        }

        if(
                value instanceof Number ||
                value instanceof Boolean
        ){

            json.append(value);
            return;
        }

        if(value instanceof Map<?, ?>){

            json.append("{");

            boolean first = true;

            for(
                    Map.Entry<?, ?> entry :
                    ((Map<?, ?>) value).entrySet()
            ){

                if(!first){
                    json.append(",");
                }

                first = false;

                json.append("\"")
                    .append(
                            escapeJson(
                                    String.valueOf(
                                            entry.getKey()
                                    )
                            )
                    )
                    .append("\":");

                appendJsonValue(
                        json,
                        entry.getValue()
                );
            }

            json.append("}");
            return;
        }

        if(
                value instanceof
                java.util.Collection<?> collection
        ){

            json.append("[");

            boolean first = true;

            for(Object item : collection){

                if(!first){
                    json.append(",");
                }

                first = false;

                appendJsonValue(
                        json,
                        item
                );
            }

            json.append("]");
            return;
        }

        json.append("\"")
            .append(
                    escapeJson(
                            String.valueOf(value)
                    )
            )
            .append("\"");
    }
    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
    private void checkDuplicateApplication(
            Long eventId,
            Long applicantId,
            String email,
            String phone
    ) {

        if(
                applicantId != null &&
                applicationRepository
                        .existsByEventIdAndApplicantId(
                                eventId,
                                applicantId
                        )
        ) {

            throw new DuplicateApplicationException(
                    "You have already submitted an application for this event."
            );
        }

        if(
                email != null &&
                !email.isBlank() &&
                applicationRepository
                        .existsByEventAndEmail(
                                eventId,
                                email
                        )
        ) {

            throw new DuplicateApplicationException(
                    "An application with this email already exists for this event."
            );
        }

        if(
                phone != null &&
                !phone.isBlank() &&
                applicationRepository
                        .existsByEventAndPhone(
                                eventId,
                                phone
                        )
        ) {

            throw new DuplicateApplicationException(
                    "An application with this phone number already exists for this event."
            );
        }
    }

    private String normalizeEmail(
            Object value
    ) {

        if(value == null){
            return null;
        }

        String email =
                String.valueOf(value)
                        .trim()
                        .toLowerCase();

        return email.isBlank()
                ? null
                : email;
    }

    private String normalizePhone(
            Object value
    ) {

        if(value == null){
            return null;
        }

        String phone =
                String.valueOf(value)
                        .trim()
                        .replaceAll(
                                "[\\s()\\-+]",
                                ""
                        );

        return phone.isBlank()
                ? null
                : phone;
    }
    private String stringAnswer(
            Map<String, Object> answers,
            String key
    ) {
        Object value = answers.get(key);

        if(value == null){
            return null;
        }

        String result =
                String.valueOf(value).trim();

        return result.isEmpty()
                ? null
                : result;
    }
    private String extractFileMetadata(
            List<FormField> fields,
            Map<String, Object> answers
    ) {

        StringBuilder json =
                new StringBuilder();

        json.append("{");

        boolean first = true;

        for(FormField field : fields) {

            String type =
                    field.getFieldType() == null
                            ? ""
                            : field.getFieldType()
                                    .name();

            if(
                    !"FILE".equals(type) &&
                    !"IMAGE".equals(type)
            ) {
                continue;
            }

            Object value =
                    answers.get(
                            field.getFieldKey()
                    );

            if(!(value instanceof Map<?, ?>)) {
                continue;
            }

            if(!first) {
                json.append(",");
            }

            first = false;

            json.append("\"")
                    .append(
                            escapeJson(
                                    field.getFieldKey()
                            )
                    )
                    .append("\":");

            appendJsonValue(
                    json,
                    value
            );
        }

        json.append("}");

        return first
                ? null
                : json.toString();
    }
    private void validateAnswers(
            List<FormField> fields,
            Map<String, Object> answers
    ) {

        // ----------------------------------------------------
        // Reject unknown keys.
        // Only fields belonging to the currently published
        // form version may be submitted.
        // ----------------------------------------------------

        java.util.Set<String> allowedKeys =
                fields.stream()
                        .map(FormField::getFieldKey)
                        .filter(java.util.Objects::nonNull)
                        .collect(
                                java.util.stream.Collectors.toSet()
                        );

        for(String submittedKey : answers.keySet()) {

            if(!allowedKeys.contains(submittedKey)) {

                throw new IllegalArgumentException(
                        "Unknown application field: " +
                                submittedKey
                );
            }
        }

        // ----------------------------------------------------
        // Validate every published form field.
        // ----------------------------------------------------

        for(FormField field : fields) {

            Object rawValue =
                    answers.get(
                            field.getFieldKey()
                    );

            // Required validation
            if(
                    field.isRequired() &&
                    isBlank(rawValue)
            ) {

                throw new IllegalArgumentException(
                        "Required field missing: " +
                                field.getLabel()
                );
            }

            // Optional + empty is valid.
            if(isBlank(rawValue)) {
                continue;
            }

            String fieldKey =
                    field.getFieldKey() == null
                            ? ""
                            : field.getFieldKey()
                                    .trim()
                                    .toLowerCase();

            String fieldType =
                    field.getFieldType() == null
                            ? ""
                            : field.getFieldType()
                                    .name()
                                    .toUpperCase();

            // Standard field validation
            validateStandardField(
                    fieldKey,
                    fieldType,
                    String.valueOf(rawValue).trim(),
                    field.getLabel()
            );

            // Configured validation rules
            validateConfiguredField(
                    fieldType,
                    String.valueOf(rawValue).trim(),
                    field
            );

            // Option/date/etc. validation
            validateStructuredField(
                    rawValue,
                    field,
                    fieldType
            );
        }
    }
    private void validateStandardField(
            String fieldKey,
            String fieldType,
            String value,
            String label
    ) {

        switch(fieldKey) {

            case "name":
            case "full_name":
            case "player_name":

                if(value.length() < 2) {
                    throw new IllegalArgumentException(
                            label +
                            " must contain at least 2 characters."
                    );
                }

                if(value.length() > 100) {
                    throw new IllegalArgumentException(
                            label +
                            " cannot exceed 100 characters."
                    );
                }

                if(!value.matches(
                        "^[\\p{L} .'-]+$"
                )) {
                    throw new IllegalArgumentException(
                            label +
                            " contains invalid characters."
                    );
                }

                break;

            case "age":

                validateAge(
                        value,
                        label
                );

                break;

            case "email":

                validateEmail(
                        value,
                        label
                );

                break;

            case "phone":

                validatePhone(
                        value,
                        label
                );

                break;

            case "position":

                if(value.length() > 100) {
                    throw new IllegalArgumentException(
                            label +
                            " cannot exceed 100 characters."
                    );
                }

                break;

            case "experience":
            case "years_experience":

                validateExperience(
                        value,
                        label
                );

                break;

            case "achievements":
            case "skills":
            case "sports_information":

                if(value.length() > 5000) {
                    throw new IllegalArgumentException(
                            label +
                            " cannot exceed 5000 characters."
                    );
                }

                break;

            default:
                break;
        }

        if("EMAIL".equals(fieldType)) {

            validateEmail(
                    value,
                    label
            );
        }

        if("PHONE".equals(fieldType)) {

            validatePhone(
                    value,
                    label
            );
        }

        if("NUMBER".equals(fieldType)) {

            try {

                new java.math.BigDecimal(value);

            } catch(NumberFormatException exception) {

                throw new IllegalArgumentException(
                        label +
                        " must be a valid number."
                );
            }
        }
    }

    private void validateAge(
            String value,
            String label
    ) {

        final int age;

        try {

            age =
                    Integer.parseInt(value);

        } catch(NumberFormatException exception) {

            throw new IllegalArgumentException(
                    label +
                    " must be a valid whole number."
            );
        }

        if(age < 5 || age > 100) {

            throw new IllegalArgumentException(
                    label +
                    " must be between 5 and 100."
            );
        }
    }

    private void validateEmail(
            String value,
            String label
    ) {

        if(value.length() > 320) {

            throw new IllegalArgumentException(
                    label +
                    " cannot exceed 320 characters."
            );
        }

        if(!value.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        )) {

            throw new IllegalArgumentException(
                    "Invalid email for field: " +
                            label
            );
        }
    }

    private void validatePhone(
            String value,
            String label
    ) {

        String normalized =
                value.replaceAll(
                        "[\\s()\\-+]",
                        ""
                );

        if(
                !normalized.matches(
                        "^\\d{7,15}$"
                )
        ) {

            throw new IllegalArgumentException(
                    "Invalid phone number for field: " +
                            label
            );
        }
    }

    private void validateExperience(
            String value,
            String label
    ) {

        try {

            java.math.BigDecimal experience =
                    new java.math.BigDecimal(value);

            if(
                    experience.compareTo(
                            java.math.BigDecimal.ZERO
                    ) < 0
            ) {

                throw new IllegalArgumentException(
                        label +
                        " cannot be negative."
                );
            }

            if(
                    experience.compareTo(
                            new java.math.BigDecimal("80")
                    ) > 0
            ) {

                throw new IllegalArgumentException(
                        label +
                        " value is unrealistic."
                );
            }

        } catch(NumberFormatException exception) {

            throw new IllegalArgumentException(
                    label +
                    " must be a valid number."
            );
        }
    }

    private void validateStructuredField(
            Object rawValue,
            FormField field,
            String fieldType
    ) {

        if("DATE".equals(fieldType)) {

            String value =
                    String.valueOf(rawValue).trim();

            try {

                java.time.LocalDate.parse(value);

            } catch(
                    java.time.format.DateTimeParseException
                    exception
            ) {

                throw new IllegalArgumentException(
                        field.getLabel() +
                                " must be a valid date."
                );
            }
        }

        if(
                "SELECT".equals(fieldType) ||
                "DROPDOWN".equals(fieldType) ||
                "RADIO".equals(fieldType) ||
                "CHECKBOX".equals(fieldType) ||
                "MULTI_SELECT".equals(fieldType)
        ) {

            validateOptionValue(
                    rawValue,
                    field
            );
        }

        if(
                "FILE".equals(fieldType) ||
                "IMAGE".equals(fieldType)
        ) {

            validateFileMetadata(
                    rawValue,
                    field,
                    fieldType
            );
        }
    }
    private void validateFileMetadata(
            Object rawValue,
            FormField field,
            String fieldType
    ) {

        if(!(rawValue instanceof Map<?, ?>)) {

            throw new IllegalArgumentException(
                    field.getLabel() +
                            " must contain file metadata."
            );
        }

        Map<?, ?> metadata =
                (Map<?, ?>) rawValue;

        Object nameObject =
                metadata.get("name");

        Object typeObject =
                metadata.get("type");

        Object sizeObject =
                metadata.get("size");

        if(
                nameObject == null ||
                typeObject == null ||
                sizeObject == null
        ) {

            throw new IllegalArgumentException(
                    "Incomplete file metadata for " +
                            field.getLabel()
            );
        }

        String fileName =
                String.valueOf(
                        nameObject
                ).trim();

        String mimeType =
                String.valueOf(
                        typeObject
                ).trim()
                .toLowerCase();

        long size;

        try {

            size =
                    Long.parseLong(
                            String.valueOf(
                                    sizeObject
                            )
                    );

        } catch(NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Invalid file size for " +
                            field.getLabel()
            );
        }

        if(fileName.isBlank()) {

            throw new IllegalArgumentException(
                    "File name is required for " +
                            field.getLabel()
            );
        }

        if(fileName.length() > 255) {

            throw new IllegalArgumentException(
                    "File name is too long for " +
                            field.getLabel()
            );
        }

        if(size < 0) {

            throw new IllegalArgumentException(
                    "File size cannot be negative."
            );
        }

        // Safe default: 10 MB
        long maxSize =
                10L * 1024L * 1024L;

        Map<String,Object> validation =
                parseValidationConfig(
                        field.getValidationConfig()
                );

        if(validation.containsKey("maxSize")) {

            try {

                maxSize =
                        Long.parseLong(
                                String.valueOf(
                                        validation.get(
                                                "maxSize"
                                        )
                                )
                        );

            } catch(NumberFormatException ignored) {
                // Keep default.
            }
        }

        if(size > maxSize) {

            throw new IllegalArgumentException(
                    "File exceeds the maximum allowed size of " +
                            maxSize +
                            " bytes."
            );
        }

        String lowerName =
                fileName.toLowerCase();

        int extensionIndex =
                lowerName.lastIndexOf(".");

        String extension =
                extensionIndex >= 0
                        ? lowerName.substring(
                                extensionIndex + 1
                        )
                        : "";

        // IMAGE fields must actually be images.
        if("IMAGE".equals(fieldType)) {

            if(!mimeType.startsWith("image/")) {

                throw new IllegalArgumentException(
                        field.getLabel() +
                                " accepts image files only."
                );
            }
        }

        if(
                "FILE".equals(fieldType) &&
                mimeType.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "File type is required for " +
                            field.getLabel()
            );
        }

        // allowedMimeTypes
        if(
                validation.containsKey(
                        "allowedMimeTypes"
                )
        ) {

            String configured =
                    String.valueOf(
                            validation.get(
                                    "allowedMimeTypes"
                            )
                    );

            java.util.Set<String> allowed =
                    new java.util.HashSet<>();

            for(
                    String item :
                    configured.split("[|,]")
            ) {

                String normalized =
                        item.trim()
                                .toLowerCase();

                if(!normalized.isBlank()) {
                    allowed.add(normalized);
                }
            }

            if(
                    !allowed.isEmpty() &&
                    !allowed.contains(mimeType)
            ) {

                throw new IllegalArgumentException(
                        "Unsupported file type for " +
                                field.getLabel()
                );
            }
        }

        // allowedExtensions
        if(
                validation.containsKey(
                        "allowedExtensions"
                )
        ) {

            String configured =
                    String.valueOf(
                            validation.get(
                                    "allowedExtensions"
                            )
                    );

            java.util.Set<String> allowed =
                    new java.util.HashSet<>();

            for(
                    String item :
                    configured.split("[|,]")
            ) {

                String normalized =
                        item.trim()
                                .toLowerCase()
                                .replace(
                                        ".",
                                        ""
                                );

                if(!normalized.isBlank()) {
                    allowed.add(normalized);
                }
            }

            if(
                    !allowed.isEmpty() &&
                    !allowed.contains(extension)
            ) {

                throw new IllegalArgumentException(
                        "Unsupported file extension for " +
                                field.getLabel()
                );
            }
        }
    }
    private void validateOptionValue(
            Object rawValue,
            FormField field
    ) {

        java.util.List<String> allowedOptions =
                parseOptions(
                        field.getOptionsConfig()
                );

        // If no options are configured, there is nothing
        // meaningful to validate here.
        if(allowedOptions.isEmpty()) {
            return;
        }

        String fieldType =
                field.getFieldType() == null
                        ? ""
                        : field.getFieldType()
                                .name()
                                .toUpperCase();

        // ----------------------------------------------------
        // MULTI_SELECT
        // ----------------------------------------------------

        if("MULTI_SELECT".equals(fieldType)) {

            if(
                    !(rawValue
                            instanceof java.util.Collection<?>)
            ) {

                throw new IllegalArgumentException(
                        field.getLabel() +
                                " must contain multiple options."
                );
            }

            for(
                    Object selected :
                    (java.util.Collection<?>)
                            rawValue
            ) {

                String selectedValue =
                        String.valueOf(selected);

                if(
                        !allowedOptions.contains(
                                selectedValue
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Invalid option for " +
                                    field.getLabel() +
                                    ": " +
                                    selectedValue
                    );
                }
            }

            return;
        }

        // ----------------------------------------------------
        // SINGLE OPTION
        // ----------------------------------------------------

        String selectedValue =
                String.valueOf(rawValue)
                        .trim();

        if(
                !allowedOptions.contains(
                        selectedValue
                )
        ) {

            throw new IllegalArgumentException(
                    "Invalid option for " +
                            field.getLabel() +
                            ": " +
                            selectedValue
            );
        }
    }

    private java.util.List<String> parseOptions(
            String optionsConfig
    ) {

        java.util.List<String> options =
                new java.util.ArrayList<>();

        if(
                optionsConfig == null ||
                optionsConfig.isBlank()
        ) {

            return options;
        }

        String config =
                optionsConfig.trim();

        // Supports:
        // ["A","B","C"]
        // ["A", "B", "C"]
        // A,B,C

        if(
                config.startsWith("[") &&
                config.endsWith("]")
        ) {

            config =
                    config.substring(
                            1,
                            config.length() - 1
                    );
        }

        if(config.isBlank()) {
            return options;
        }

        for(
                String raw :
                config.split(",")
        ) {

            String option =
                    raw
                            .replace("\"", "")
                            .trim();

            if(!option.isEmpty()) {
                options.add(option);
            }
        }

        return options;
    }
    private void validateConfiguredField(
            String fieldType,
            String value,
            FormField field
    ) {

        Map<String,Object> config =
                parseValidationConfig(
                        field.getValidationConfig()
                );

        if(config.containsKey("minLength")) {

            int minLength =
                    Integer.parseInt(
                            String.valueOf(
                                    config.get(
                                            "minLength"
                                    )
                            )
                    );

            if(value.length() < minLength) {

                throw new IllegalArgumentException(
                        "Minimum " +
                                minLength +
                                " characters required for " +
                                field.getLabel()
                );
            }
        }

        if(config.containsKey("maxLength")) {

            int maxLength =
                    Integer.parseInt(
                            String.valueOf(
                                    config.get(
                                            "maxLength"
                                    )
                            )
                    );

            if(value.length() > maxLength) {

                throw new IllegalArgumentException(
                        "Maximum " +
                                maxLength +
                                " characters allowed for " +
                                field.getLabel()
                );
            }
        }

        if(
                "NUMBER".equals(fieldType) &&
                config.containsKey("min")
        ) {

            java.math.BigDecimal number =
                    new java.math.BigDecimal(
                            value
                    );

            java.math.BigDecimal min =
                    new java.math.BigDecimal(
                            String.valueOf(
                                    config.get(
                                            "min"
                                    )
                            )
                    );

            if(number.compareTo(min) < 0) {

                throw new IllegalArgumentException(
                        "Minimum value for " +
                                field.getLabel() +
                                " is " +
                                min
                );
            }
        }

        if(
                "NUMBER".equals(fieldType) &&
                config.containsKey("max")
        ) {

            java.math.BigDecimal number =
                    new java.math.BigDecimal(
                            value
                    );

            java.math.BigDecimal max =
                    new java.math.BigDecimal(
                            String.valueOf(
                                    config.get(
                                            "max"
                                    )
                            )
                    );

            if(number.compareTo(max) > 0) {

                throw new IllegalArgumentException(
                        "Maximum value for " +
                                field.getLabel() +
                                " is " +
                                max
                );
            }
        }
    }

    private Map<String,Object> parseValidationConfig(
            String validationConfig
    ) {

        Map<String,Object> result =
                new java.util.HashMap<>();

        if(
                validationConfig == null ||
                validationConfig.isBlank()
        ) {
            return result;
        }

        String config =
                validationConfig.trim();

        if(
                config.startsWith("{") &&
                config.endsWith("}")
        ) {

            String body =
                    config.substring(
                            1,
                            config.length() - 1
                    ).trim();

            if(!body.isEmpty()) {

                for(
                        String entry :
                        body.split(",")
                ) {

                    String[] parts =
                            entry.split(
                                    ":",
                                    2
                            );

                    if(parts.length == 2) {

                        String key =
                                parts[0]
                                        .replace(
                                                "\"",
                                                ""
                                        )
                                        .trim();

                        String value =
                                parts[1]
                                        .replace(
                                                "\"",
                                                ""
                                        )
                                        .trim();

                        result.put(
                                key,
                                value
                        );
                    }
                }
            }
        }

        return result;
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