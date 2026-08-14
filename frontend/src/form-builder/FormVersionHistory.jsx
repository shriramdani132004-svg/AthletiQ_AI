function FormVersionHistory({
    versions = [],
    selectedVersionId = null,
    onSelect
}) {
    const orderedVersions = Array.isArray(versions)
        ? versions.slice().sort((a, b) =>
            Number(b.versionNumber ?? b.version ?? 0) -
            Number(a.versionNumber ?? a.version ?? 0)
        )
        : [];

    return (
        <section className="form-version-history">
            <header>
                <h2>Form Version History</h2>
                <p>
                    Select a version to inspect its historical form configuration.
                </p>
            </header>

            {orderedVersions.length === 0 ? (
                <p>No form versions available.</p>
            ) : (
                <ol>
                    {orderedVersions.map(version => {
                        const id = version.id ?? version.versionId;
                        const versionNumber =
                            version.versionNumber ?? version.version ?? id;

                        const status = String(
                            version.status ??
                            version.formVersionStatus ??
                            "DRAFT"
                        ).toUpperCase();

                        const selected =
                            String(id) === String(selectedVersionId);

                        const immutable =
                            status === "PUBLISHED" ||
                            status === "ARCHIVED";

                        return (
                            <li key={id ?? versionNumber}>
                                <button
                                    type="button"
                                    onClick={() => onSelect?.(id)}
                                    aria-pressed={selected}
                                >
                                    Version {versionNumber} ({status})
                                </button>

                                {selected && (
                                    <span>{" "}Selected</span>
                                )}

                                {immutable && (
                                    <span>{" "}Read-only</span>
                                )}
                            </li>
                        );
                    })}
                </ol>
            )}
        </section>
    );
}

export default FormVersionHistory;