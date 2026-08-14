import { OPTION_FIELD_TYPES, UPLOAD_FIELD_TYPES } from "./formConfig";

export function parseOptions(optionsConfig) {
    if (!optionsConfig) {
        return [];
    }

    return optionsConfig
        .split("`n")
        .map(option => option.trim())
        .filter(Boolean);
}

export function serializeOptions(options) {
    return (options || [])
        .map(option => String(option).trim())
        .filter(Boolean)
        .join("`n");
}

export function supportsOptions(fieldType) {
    return OPTION_FIELD_TYPES.includes(fieldType);
}

export function supportsUpload(fieldType) {
    return UPLOAD_FIELD_TYPES.includes(fieldType);
}

export function getDefaultValidation(fieldType) {
    switch (fieldType) {
        case "NUMBER":
            return { min: "", max: "", step: "" };
        case "TEXT":
        case "LONG_TEXT":
            return { minLength: "", maxLength: "", pattern: "" };
        case "EMAIL":
            return { pattern: "" };
        case "PHONE":
            return { pattern: "" };
        case "URL":
            return { pattern: "" };
        case "FILE":
        case "IMAGE":
            return { maxSizeMb: "", allowedTypes: "" };
        case "RATING":
            return { min: 1, max: 5 };
        default:
            return {};
    }
}

export function normalizeValidation(fieldType, value) {
    if (!value) {
        return getDefaultValidation(fieldType);
    }

    if (typeof value === "object") {
        return value;
    }

    try {
        return JSON.parse(value);
    } catch {
        return getDefaultValidation(fieldType);
    }
}