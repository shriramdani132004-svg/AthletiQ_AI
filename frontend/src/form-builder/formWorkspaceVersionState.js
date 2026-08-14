import { getVersionEditingState } from "./formVersionEditing";

export function getWorkspaceVersionState(version) {
    const state = getVersionEditingState(version);

    return {
        ...state,
        readOnly: !state.editable,
        canModifyFields: state.editable,
        canSave: state.editable,
        canPublish: state.canPublish,
        canCreateDraft: state.canCreateDraft
    };
}

export function assertWorkspaceCanModify(version) {
    const state = getWorkspaceVersionState(version);

    if (!state.canModifyFields) {
        throw new Error("This form version is read-only.");
    }

    return true;
}