import { canSaveDraft } from "./formSaveWorkflow";

export const SAVE_STATUS = Object.freeze({
    IDLE: "IDLE",
    DIRTY: "DIRTY",
    SAVING: "SAVING",
    SAVED: "SAVED",
    ERROR: "ERROR"
});

export function getInitialSaveState() {
    return {
        status: SAVE_STATUS.IDLE,
        message: "",
        error: null
    };
}

export function markFormDirty(state = getInitialSaveState()) {
    return {
        ...state,
        status: SAVE_STATUS.DIRTY,
        message: "Unsaved changes",
        error: null
    };
}

export function canSaveCurrentVersion(version, state) {
    if (!canSaveDraft(version)) {
        return false;
    }

    if (state?.status === SAVE_STATUS.SAVING) {
        return false;
    }

    return true;
}

export function startSave(state = getInitialSaveState()) {
    return {
        ...state,
        status: SAVE_STATUS.SAVING,
        message: "Saving...",
        error: null
    };
}

export function completeSave(state = getInitialSaveState()) {
    return {
        ...state,
        status: SAVE_STATUS.SAVED,
        message: "Saved",
        error: null
    };
}

export function failSave(state = getInitialSaveState(), error) {
    return {
        ...state,
        status: SAVE_STATUS.ERROR,
        message: "Save failed",
        error: error?.message ?? String(error ?? "Unknown save error")
    };
}