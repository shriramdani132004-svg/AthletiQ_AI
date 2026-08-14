import {
    FORM_VERSION_STATUS,
    isImmutableVersion,
    isDraftVersion
} from "./formVersionUtils";

function FormVersionSelector({
    versions = [],
    selectedVersionId = null,
    onSelect,
    onCreateVersion,
    editable = true
}) {
    const selectedVersion = versions.find(version =>
        String(version.id ?? version.versionId) === String(selectedVersionId)
    );

    const selectedIsImmutable = selectedVersion
        ? isImmutableVersion(selectedVersion)
        : false;

    const canCreateVersion = selectedVersion
        ? selectedVersion.status === FORM_VERSION_STATUS.PUBLISHED
        : false;

    return (
        <section className="form-version-selector">
            <div>
                <h2>Form Version</h2>
                <p>Select the version being edited or reviewed.</p>
            </div>

            <label>
                Version
                <select
                    value={selectedVersionId ?? ""}
                    onChange={event => onSelect?.(event.target.value)}
                >
                    <option value="" disabled>
                        Select a version
                    </option>

                    {versions.map(version => {
                        const id = version.id ?? version.versionId;

                        return (
                            <option key={id} value={id}>
                                Version {version.versionNumber} - {version.status}
                            </option>
                        );
                    })}
                </select>
            </label>

            {selectedVersion && (
                <div className="form-version-status">
                    <strong>
                        Version {selectedVersion.versionNumber}
                    </strong>
                    <span>
                        {selectedVersion.status}
                    </span>

                    {isDraftVersion(selectedVersion) && editable && (
                        <p>This draft version can be edited.</p>
                    )}

                    {selectedIsImmutable && (
                        <p>This version is immutable and read-only.</p>
                    )}
                </div>
            )}

            {canCreateVersion && (
                <button
                    type="button"
                    onClick={() => onCreateVersion?.(selectedVersion)}
                >
                    Create New Draft Version
                </button>
            )}
        </section>
    );
}

export default FormVersionSelector;