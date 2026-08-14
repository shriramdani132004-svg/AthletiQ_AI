package com.athletiq.backend.form.service;

import com.athletiq.backend.form.entity.FieldType;
import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.entity.FormVersion;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class FormValidationService {

    private static final Pattern FIELD_KEY_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{1,99}$");

    public void validateField(FormField field) {

        if (field == null) {
            throw new IllegalArgumentException(
                    "Field cannot be null"
            );
        }

        validateFieldKey(field.getFieldKey());
        validateLabel(field.getLabel());
        validateFieldType(field.getFieldType());
        validateDisplayOrder(field.getDisplayOrder());

        if (field.getDescription() != null &&
                field.getDescription().length() > 1000) {

            throw new IllegalArgumentException(
                    "Field description cannot exceed 1000 characters"
            );
        }

        validateFieldConfiguration(field);
    }

    public void validateVersionFields(
            FormVersion version,
            List<FormField> fields
    ) {

        if (version == null) {
            throw new IllegalArgumentException(
                    "Form version cannot be null"
            );
        }

        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Form must contain at least one field"
            );
        }

        Set<String> keys = new HashSet<>();
        Set<Integer> orders = new HashSet<>();

        for (FormField field : fields) {

            validateField(field);

            if (!keys.add(field.getFieldKey())) {
                throw new IllegalArgumentException(
                        "Duplicate field key: "
                                + field.getFieldKey()
                );
            }

            if (!orders.add(field.getDisplayOrder())) {
                throw new IllegalArgumentException(
                        "Duplicate display order: "
                                + field.getDisplayOrder()
                );
            }
        }
    }

    private void validateFieldKey(String fieldKey) {

        if (fieldKey == null ||
                fieldKey.isBlank()) {

            throw new IllegalArgumentException(
                    "Field key is required"
            );
        }

        if (!FIELD_KEY_PATTERN.matcher(fieldKey).matches()) {

            throw new IllegalArgumentException(
                    "Field key must start with a letter and contain only letters, numbers, and underscores"
            );
        }
    }

    private void validateLabel(String label) {

        if (label == null ||
                label.isBlank()) {

            throw new IllegalArgumentException(
                    "Field label is required"
            );
        }

        if (label.length() > 255) {

            throw new IllegalArgumentException(
                    "Field label cannot exceed 255 characters"
            );
        }
    }

    private void validateFieldType(FieldType fieldType) {

        if (fieldType == null) {

            throw new IllegalArgumentException(
                    "Field type is required"
            );
        }
    }

    private void validateDisplayOrder(Integer displayOrder) {

        if (displayOrder == null ||
                displayOrder < 0) {

            throw new IllegalArgumentException(
                    "Display order must be zero or greater"
            );
        }
    }

    private void validateFieldConfiguration(
            FormField field
    ) {

        FieldType type = field.getFieldType();

        if (requiresOptions(type) &&
                (field.getOptionsConfig() == null ||
                 field.getOptionsConfig().isBlank())) {

            throw new IllegalArgumentException(
                    "Options configuration is required for "
                            + type
            );
        }

        if (type == FieldType.RATING) {

            if (field.getValidationConfig() == null ||
                    field.getValidationConfig().isBlank()) {

                throw new IllegalArgumentException(
                        "Rating fields require validation configuration"
                );
            }
        }

        if ((type == FieldType.FILE ||
             type == FieldType.IMAGE) &&
            field.getValidationConfig() == null) {

            throw new IllegalArgumentException(
                    "File and image fields require validation configuration"
            );
        }
    }

    private boolean requiresOptions(FieldType type) {

        return type == FieldType.DROPDOWN ||
                type == FieldType.RADIO ||
                type == FieldType.CHECKBOX ||
                type == FieldType.MULTI_SELECT;
    }
}