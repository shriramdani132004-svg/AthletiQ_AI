export function validateFieldConfiguration(field) {
    const errors = [];

    if (!field.fieldKey?.trim()) {
        errors.push("Field key is required.");
    }

    if (!field.label?.trim()) {
        errors.push("Field label is required.");
    }

    if (field.displayOrder === undefined || field.displayOrder === null) {
        errors.push("Display order is required.");
    }

    const optionTypes = [
        "DROPDOWN",
        "RADIO",
        "CHECKBOX",
        "MULTI_SELECT"
    ];

    if (optionTypes.includes(field.fieldType)) {
        if (!field.optionsConfig?.trim()) {
            errors.push("Options are required for this field type.");
        }
    }

    if (field.fieldType === "NUMBER" && field.validationConfig) {
        try {
            const config = JSON.parse(field.validationConfig);

            if (config.min !== "" && config.max !== "" &&
                Number(config.min) > Number(config.max)) {
                errors.push("Minimum cannot be greater than maximum.");
            }
        } catch {
            errors.push("Invalid number validation configuration.");
        }
    }

    if (field.fieldType === "RATING" && field.validationConfig) {
        try {
            const config = JSON.parse(field.validationConfig);

            if (Number(config.min) >= Number(config.max)) {
                errors.push("Rating minimum must be lower than maximum.");
            }
        } catch {
            errors.push("Invalid rating validation configuration.");
        }
    }

    return errors;
}