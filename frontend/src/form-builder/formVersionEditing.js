import {
    FORM_VERSION_STATUS,
    isDraftVersion,
    isImmutableVersion,
    assertVersionEditable,
    createVersionFromPublished
} from "./formVersionUtils";

export function getVersionEditingState(version) {
    if (!version) {
        return {
            status: null,
            editable: false,
            immutable: false,
            canPublish: false,
            canCreateDraft: false
        };
    }

    const draft = isDraftVersion(version);
    const immutable = isImmutableVersion(version);

    return {
        status: version.status,
        editable: draft,
        immutable,
        canPublish: draft,
        canCreateDraft:
            version.status === FORM_VERSION_STATUS.PUBLISHED
    };
}

export function assertEditableVersion(version) {
    return assertVersionEditable(version);
}

export function prepareNewDraftVersion(publishedVersion, versions = []) {
    if (publishedVersion?.status !== FORM_VERSION_STATUS.PUBLISHED) {
        throw new Error(
            "A new draft can only be created from a published version."
        );
    }

    return createVersionFromPublished(
        publishedVersion,
        versions
    );
}