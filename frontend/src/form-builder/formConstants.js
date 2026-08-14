export const FORM_VERSION_STATUS = Object.freeze({
    DRAFT: "DRAFT",
    PUBLISHED: "PUBLISHED",
    LOCKED: "LOCKED"
});

export const FORM_FIELD_TYPES = Object.freeze([
    "TEXT",
    "NUMBER",
    "EMAIL",
    "PHONE",
    "DATE",
    "DROPDOWN",
    "RADIO",
    "CHECKBOX",
    "MULTI_SELECT",
    "RATING",
    "FILE",
    "IMAGE",
    "URL",
    "LONG_TEXT"
]);

export const OPTION_FIELD_TYPES = Object.freeze([
    "DROPDOWN",
    "RADIO",
    "CHECKBOX",
    "MULTI_SELECT"
]);

export const UPLOAD_FIELD_TYPES = Object.freeze([
    "FILE",
    "IMAGE"
]);

export function isDraftVersion(version) {
    return version?.status === FORM_VERSION_STATUS.DRAFT;
}

export function isPublishedVersion(version) {
    return version?.status === FORM_VERSION_STATUS.PUBLISHED;
}

export function isLockedVersion(version) {
    return version?.status === FORM_VERSION_STATUS.LOCKED;
}

export function requiresOptions(fieldType) {
    return OPTION_FIELD_TYPES.includes(fieldType);
}

export function isUploadField(fieldType) {
    return UPLOAD_FIELD_TYPES.includes(fieldType);
}