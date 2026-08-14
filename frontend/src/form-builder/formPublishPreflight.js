import { validateFieldConfiguration } from "./fieldValidation";

const DRAFT_STATUS = "DRAFT";
const PUBLISHED_STATUS = "PUBLISHED";

export function isDraftVersion(version) {
    return version?.status === DRAFT_STATUS;
}

export function isPublishedVersion(version) {
    return version?.status === PUBLISHED_STATUS;
}

export function validateFormBeforeSave(fields = []) {
    const errors = [];

    if (!Array.isArray(fields)) {
        return ["Form fields must be an array."];
    }

    fields.forEach((field, index) => {
        const fieldErrors = validateFieldConfiguration({
            ...field,
            displayOrder: field.displayOrder ?? index
        });

        fieldErrors.forEach(error => {
            errors.push(`Field ${index + 1}: ${error}`);
        });
    });

    return errors;
}

export function validateFormBeforePublish(version, fields = []) {
    const errors = [];

    if (!version) {
        errors.push("Form version is required.");
        return errors;
    }

    if (!isDraftVersion(version)) {
        errors.push("Only DRAFT form versions can be published.");
    }

    if (!Array.isArray(fields) || fields.length === 0) {
        errors.push("A form must contain at least one field.");
    }

    errors.push(...validateFormBeforeSave(fields));

    const orders = fields.map(field => Number(field.displayOrder));

    const invalidOrders = orders.some(order =>
        !Number.isInteger(order) || order < 0
    );

    if (invalidOrders) {
        errors.push("Every field must have a valid display order.");
    }

    const keys = fields.map(field => String(field.fieldKey || "").trim());
    const nonEmptyKeys = keys.filter(Boolean);

    if (nonEmptyKeys.length !== keys.length) {
        errors.push("Every field must have a field key.");
    }

    if (new Set(nonEmptyKeys).size !== nonEmptyKeys.length) {
        errors.push("Field keys must be unique.");
    }

    return errors;
}

export function canEditFormVersion(version) {
    return isDraftVersion(version);
}

export function canPublishFormVersion(version, fields = []) {
    return validateFormBeforePublish(version, fields).length === 0;
}