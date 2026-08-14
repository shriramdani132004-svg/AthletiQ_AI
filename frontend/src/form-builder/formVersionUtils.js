export const FORM_VERSION_STATUS = Object.freeze({
    DRAFT: "DRAFT",
    PUBLISHED: "PUBLISHED",
    ARCHIVED: "ARCHIVED"
});

export function isDraftVersion(version) {
    return version?.status === FORM_VERSION_STATUS.DRAFT;
}

export function isPublishedVersion(version) {
    return version?.status === FORM_VERSION_STATUS.PUBLISHED;
}

export function isArchivedVersion(version) {
    return version?.status === FORM_VERSION_STATUS.ARCHIVED;
}

export function isImmutableVersion(version) {
    return isPublishedVersion(version) || isArchivedVersion(version);
}

export function canEditVersion(version) {
    return isDraftVersion(version);
}

export function canPublishVersion(version) {
    return isDraftVersion(version);
}

export function getNextVersionNumber(versions = []) {
    if (!Array.isArray(versions) || versions.length === 0) {
        return 1;
    }

    const numbers = versions
        .map(version => Number(version.versionNumber))
        .filter(Number.isInteger);

    if (numbers.length === 0) {
        return 1;
    }

    return Math.max(...numbers) + 1;
}

export function createDraftVersionTemplate(form, versions = []) {
    if (!form) {
        throw new Error("Form is required.");
    }

    return {
        formId: form.id ?? form.formId,
        versionNumber: getNextVersionNumber(versions),
        status: FORM_VERSION_STATUS.DRAFT,
        sourceVersionId: null
    };
}

export function createVersionFromPublished(publishedVersion, versions = []) {
    if (!isPublishedVersion(publishedVersion)) {
        throw new Error("Only a published version can be used as a version source.");
    }

    return {
        formId: publishedVersion.formId,
        versionNumber: getNextVersionNumber(versions),
        status: FORM_VERSION_STATUS.DRAFT,
        sourceVersionId: publishedVersion.id ?? publishedVersion.versionId
    };
}

export function assertVersionEditable(version) {
    if (!canEditVersion(version)) {
        throw new Error("Published or archived form versions are immutable.");
    }

    return true;
}