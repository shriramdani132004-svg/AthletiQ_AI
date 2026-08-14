import { validateFormBeforeSave } from "./formPublishPreflight";
import { assertWorkspaceCanModify } from "./formWorkspaceVersionState";

export function prepareDraftSave(version, fields = []) {
    assertWorkspaceCanModify(version);

    const errors = validateFormBeforeSave(fields);

    if (errors.length > 0) {
        return {
            valid: false,
            errors,
            payload: null
        };
    }

    return {
        valid: true,
        errors: [],
        payload: {
            formVersionId: version.id ?? version.versionId,
            fields
        }
    };
}

export async function saveDraftVersion({
    version,
    fields = [],
    saveFields
}) {
    const prepared = prepareDraftSave(version, fields);

    if (!prepared.valid) {
        return prepared;
    }

    if (typeof saveFields !== "function") {
        throw new Error("saveFields operation is required.");
    }

    const result = await saveFields(
        prepared.payload.formVersionId,
        prepared.payload.fields
    );

    return {
        valid: true,
        errors: [],
        payload: prepared.payload,
        result
    };
}

export function canSaveDraft(version) {
    try {
        assertWorkspaceCanModify(version);
        return true;
    } catch {
        return false;
    }
}