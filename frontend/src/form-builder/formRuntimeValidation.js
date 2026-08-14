export function parseValidationConfig(field) {
    if (!field?.validationConfig) {
        return {};
    }

    if (typeof field.validationConfig === "object") {
        return field.validationConfig;
    }

    try {
        return JSON.parse(field.validationConfig);
    } catch {
        return {};
    }
}

export function validateFieldValue(field, value) {
    const errors = [];
    const config = parseValidationConfig(field);

    const empty = value === undefined ||
        value === null ||
        String(value).trim() === "";

    if (field?.required && empty) {
        errors.push("This field is required.");
        return errors;
    }

    if (empty) {
        return errors;
    }

    switch (field.fieldType) {
        case "NUMBER": {
            const numberValue = Number(value);

            if (Number.isNaN(numberValue)) {
                errors.push("Value must be a valid number.");
                break;
            }

            if (config.min !== "" &&
                config.min !== undefined &&
                numberValue < Number(config.min)) {
                errors.push(`Value must be at least ${config.min}.`);
            }

            if (config.max !== "" &&
                config.max !== undefined &&
                numberValue > Number(config.max)) {
                errors.push(`Value must be at most ${config.max}.`);
            }

            break;
        }

        case "TEXT":
        case "LONG_TEXT": {
            const text = String(value);

            if (config.minLength !== "" &&
                config.minLength !== undefined &&
                text.length < Number(config.minLength)) {
                errors.push(`Minimum length is ${config.minLength}.`);
            }

            if (config.maxLength !== "" &&
                config.maxLength !== undefined &&
                text.length > Number(config.maxLength)) {
                errors.push(`Maximum length is ${config.maxLength}.`);
            }

            if (config.pattern) {
                try {
                    if (!new RegExp(config.pattern).test(text)) {
                        errors.push("Value does not match the required pattern.");
                    }
                } catch {
                    errors.push("Field pattern configuration is invalid.");
                }
            }

            break;
        }

        case "EMAIL": {
            const emailPattern = /^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$/;

            if (!emailPattern.test(String(value))) {
                errors.push("Enter a valid email address.");
            }

            break;
        }

        case "URL": {
            try {
                new URL(String(value));
            } catch {
                errors.push("Enter a valid URL.");
            }

            break;
        }

        case "RATING": {
            const rating = Number(value);
            const min = Number(config.min ?? 1);
            const max = Number(config.max ?? 5);

            if (Number.isNaN(rating)) {
                errors.push("Rating must be a number.");
                break;
            }

            if (rating < min || rating > max) {
                errors.push(`Rating must be between ${min} and ${max}.`);
            }

            break;
        }

        case "DROPDOWN":
        case "RADIO":
        case "CHECKBOX":
        case "MULTI_SELECT": {
            if (field.optionsConfig) {
                const options = String(field.optionsConfig)
                    .split("`n")
                    .map(option => option.trim())
                    .filter(Boolean);

                const values = Array.isArray(value)
                    ? value
                    : [value];

                const invalid = values.some(item =>
                    !options.includes(String(item))
                );

                if (invalid) {
                    errors.push("Selection contains an invalid option.");
                }
            }

            break;
        }

        case "FILE":
        case "IMAGE": {
            if (config.maxSizeMb && value?.size) {
                const maxBytes = Number(config.maxSizeMb) * 1024 * 1024;

                if (value.size > maxBytes) {
                    errors.push(`File must be smaller than ${config.maxSizeMb} MB.`);
                }
            }

            if (config.allowedTypes && value?.name) {
                const allowed = String(config.allowedTypes)
                    .split(",")
                    .map(type => type.trim().toLowerCase())
                    .filter(Boolean);

                const extension = value.name.includes(".")
                    ? value.name.split(".").pop().toLowerCase()
                    : "";

                if (allowed.length > 0 && !allowed.includes(extension)) {
                    errors.push("File type is not allowed.");
                }
            }

            break;
        }

        default:
            break;
    }

    if (config.pattern &&
        ["EMAIL", "PHONE", "URL"].includes(field.fieldType)) {
        try {
            if (!new RegExp(config.pattern).test(String(value))) {
                errors.push("Value does not match the configured pattern.");
            }
        } catch {
            errors.push("Configured validation pattern is invalid.");
        }
    }

    return errors;
}

export function validateFormValues(fields = [], values = {}) {
    const errors = {};

    for (const field of fields) {
        const fieldErrors = validateFieldValue(
            field,
            values[field.fieldKey]
        );

        if (fieldErrors.length > 0) {
            errors[field.fieldKey] = fieldErrors;
        }
    }

    return errors;
}

export function hasFormValidationErrors(errors = {}) {
    return Object.keys(errors).length > 0;
}