import { SAVE_STATUS } from "./formSaveState";

function FormSaveBar({
    saveState,
    saveDisabled = false,
    onSave
}) {
    const status = saveState?.status ?? SAVE_STATUS.IDLE;
    const saving = status === SAVE_STATUS.SAVING;
    const saved = status === SAVE_STATUS.SAVED;
    const dirty = status === SAVE_STATUS.DIRTY;
    const error = status === SAVE_STATUS.ERROR;

    return (
        <div className="form-save-bar">
            <div className="form-save-status">
                {dirty && <span>Unsaved changes</span>}
                {saving && <span>Saving...</span>}
                {saved && <span>Saved</span>}
                {error && <span>Save failed</span>}
                {status === SAVE_STATUS.IDLE && <span>All changes saved</span>}

                {error && saveState?.error && (
                    <small>{saveState.error}</small>
                )}
            </div>

            <button
                type="button"
                disabled={saveDisabled || saving || !dirty}
                onClick={onSave}
            >
                {saving ? "Saving..." : "Save Draft"}
            </button>
        </div>
    );
}

export default FormSaveBar;