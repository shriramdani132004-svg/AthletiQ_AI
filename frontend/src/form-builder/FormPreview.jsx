function parseOptions(optionsConfig) {
    if (!optionsConfig) return [];

    try {
        const parsed = JSON.parse(optionsConfig);

        if (Array.isArray(parsed)) {
            return parsed
                .map(option =>
                    typeof option === "object"
                        ? String(option.value ?? option.label ?? "")
                        : String(option)
                )
                .map(option => option.trim())
                .filter(Boolean);
        }
    } catch {
        return String(optionsConfig)
            .split(/[\r\n,]+/)
            .map(option => option.trim())
            .filter(Boolean);
    }

    return [];
}

function FormPreview({ fields = [] }) {
    const orderedFields = fields
        .slice()
        .sort(
            (a, b) =>
                Number(a.displayOrder ?? 0) -
                Number(b.displayOrder ?? 0)
        );

    return (
        <section>
            <h2>Form Preview</h2>

            {orderedFields.length === 0 ? (
                <p>Add fields to preview the form.</p>
            ) : (
                <form className="form-preview">

                    {orderedFields.map(field => {
                        const type = String(
                            field.fieldType || "TEXT"
                        ).toUpperCase();

                        const options =
                            parseOptions(
                                field.optionsConfig
                            );

                        return (
                            <div
                                key={field.id}
                                className="form-preview-field"
                            >
                                <label>
                                    {field.label}
                                    {field.required && (
                                        <span> *</span>
                                    )}
                                </label>

                                {field.description && (
                                    <small>
                                        {field.description}
                                    </small>
                                )}

                                {type === "LONG_TEXT" ? (
                                    <textarea />
                                ) : type === "DROPDOWN" ||
                                  type === "SELECT" ? (
                                    <select defaultValue="">
                                        <option value="" disabled>
                                            Select an option
                                        </option>

                                        {options.map(option => (
                                            <option
                                                key={option}
                                                value={option}
                                            >
                                                {option}
                                            </option>
                                        ))}
                                    </select>
                                ) : type === "MULTI_SELECT" ? (
                                    <div className="form-preview-options">
                                        {options.map(option => (
                                            <label
                                                key={option}
                                                className="form-preview-option"
                                            >
                                                <input
                                                    type="checkbox"
                                                    value={option}
                                                />
                                                <span>
                                                    {option}
                                                </span>
                                            </label>
                                        ))}
                                    </div>
                                ) : type === "CHECKBOX" ? (
                                    <label className="form-preview-option">
                                        <input type="checkbox" />
                                        <span>
                                            {field.description ||
                                                field.label}
                                        </span>
                                    </label>
                                ) : type === "RADIO" ? (
                                    <div className="form-preview-options">
                                        {options.map(option => (
                                            <label
                                                key={option}
                                                className="form-preview-option"
                                            >
                                                <input
                                                    type="radio"
                                                    name={
                                                        field.fieldKey
                                                    }
                                                    value={option}
                                                />
                                                <span>
                                                    {option}
                                                </span>
                                            </label>
                                        ))}
                                    </div>
                                ) : type === "FILE" ||
                                  type === "IMAGE" ? (
                                    <input type="file" />
                                ) : type === "DATE" ? (
                                    <input type="date" />
                                ) : type === "NUMBER" ? (
                                    <input type="number" />
                                ) : type === "EMAIL" ? (
                                    <input type="email" />
                                ) : type === "URL" ? (
                                    <input type="url" />
                                ) : type === "PHONE" ? (
                                    <input type="tel" />
                                ) : type === "RATING" ? (
                                    <input
                                        type="number"
                                        min="1"
                                        max="5"
                                    />
                                ) : (
                                    <input type="text" />
                                )}
                            </div>
                        );
                    })}

                    <button
                        type="button"
                        disabled
                    >
                        Submit Preview
                    </button>
                </form>
            )}
        </section>
    );
}

export default FormPreview;