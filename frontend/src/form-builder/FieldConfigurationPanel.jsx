import { useMemo } from "react";
import { FIELD_TYPES } from "../api/formApi";
import { getDefaultValidation, normalizeValidation, parseOptions, serializeOptions } from "./fieldConfigUtils";
import { OPTION_FIELD_TYPES, UPLOAD_FIELD_TYPES } from "./formConfig";

function FieldConfigurationPanel({ field, onChange }) {
    const validation = useMemo(
        () => normalizeValidation(field.fieldType, field.validationConfig),
        [field.fieldType, field.validationConfig]
    );

    const update = (name, value) => {
        onChange({
            ...field,
            [name]: value
        });
    };

    const updateValidation = (name, value) => {
        const next = {
            ...validation,
            [name]: value
        };

        update("validationConfig", JSON.stringify(next));
    };

    const hasOptions = OPTION_FIELD_TYPES.includes(field.fieldType);
    const isUpload = UPLOAD_FIELD_TYPES.includes(field.fieldType);

    return (
        <section>
            <h3>Field Configuration</h3>

            <label>
                Field Type
                <select
                    value={field.fieldType || "TEXT"}
                    onChange={event => {
                        const type = event.target.value;
                        onChange({
                            ...field,
                            fieldType: type,
                            validationConfig: JSON.stringify(
                                getDefaultValidation(type)
                            )
                        });
                    }}
                >
                    {FIELD_TYPES.map(type => (
                        <option key={type.value} value={type.value}>
                            {type.label}
                        </option>
                    ))}
                </select>
            </label>

            <label>
                Label
                <input
                    value={field.label || ""}
                    onChange={event => update("label", event.target.value)}
                />
            </label>

            <label>
                Description
                <textarea
                    value={field.description || ""}
                    onChange={event => update("description", event.target.value)}
                />
            </label>

            <label>
                <input
                    type="checkbox"
                    checked={Boolean(field.required)}
                    onChange={event => update("required", event.target.checked)}
                />
                Required
            </label>

            {hasOptions && (
                <label>
                    Options
                    <textarea
                        value={serializeOptions(parseOptions(field.optionsConfig))}
                        placeholder={"One option per line"}
                        onChange={event => update("optionsConfig", event.target.value)}
                    />
                </label>
            )}

            {field.fieldType === "NUMBER" && (
                <div>
                    <h4>Number Validation</h4>
                    <label>
                        Minimum
                        <input
                            type="number"
                            value={validation.min ?? ""}
                            onChange={event => updateValidation("min", event.target.value)}
                        />
                    </label>
                    <label>
                        Maximum
                        <input
                            type="number"
                            value={validation.max ?? ""}
                            onChange={event => updateValidation("max", event.target.value)}
                        />
                    </label>
                    <label>
                        Step
                        <input
                            type="number"
                            value={validation.step ?? ""}
                            onChange={event => updateValidation("step", event.target.value)}
                        />
                    </label>
                </div>
            )}

            {(field.fieldType === "TEXT" || field.fieldType === "LONG_TEXT") && (
                <div>
                    <h4>Text Validation</h4>
                    <label>
                        Minimum Length
                        <input
                            type="number"
                            min="0"
                            value={validation.minLength ?? ""}
                            onChange={event => updateValidation("minLength", event.target.value)}
                        />
                    </label>
                    <label>
                        Maximum Length
                        <input
                            type="number"
                            min="0"
                            value={validation.maxLength ?? ""}
                            onChange={event => updateValidation("maxLength", event.target.value)}
                        />
                    </label>
                    <label>
                        Pattern
                        <input
                            value={validation.pattern ?? ""}
                            onChange={event => updateValidation("pattern", event.target.value)}
                        />
                    </label>
                </div>
            )}

            {(field.fieldType === "EMAIL" || field.fieldType === "PHONE" || field.fieldType === "URL") && (
                <div>
                    <h4>Format Validation</h4>
                    <label>
                        Pattern
                        <input
                            value={validation.pattern ?? ""}
                            onChange={event => updateValidation("pattern", event.target.value)}
                        />
                    </label>
                </div>
            )}

            {field.fieldType === "RATING" && (
                <div>
                    <h4>Rating Range</h4>
                    <label>
                        Minimum
                        <input
                            type="number"
                            value={validation.min ?? 1}
                            onChange={event => updateValidation("min", Number(event.target.value))}
                        />
                    </label>
                    <label>
                        Maximum
                        <input
                            type="number"
                            value={validation.max ?? 5}
                            onChange={event => updateValidation("max", Number(event.target.value))}
                        />
                    </label>
                </div>
            )}

            {isUpload && (
                <div>
                    <h4>Upload Validation</h4>
                    <label>
                        Maximum Size MB
                        <input
                            type="number"
                            min="1"
                            value={validation.maxSizeMb ?? ""}
                            onChange={event => updateValidation("maxSizeMb", event.target.value)}
                        />
                    </label>
                    <label>
                        Allowed File Types
                        <input
                            placeholder="pdf,jpg,png"
                            value={validation.allowedTypes ?? ""}
                            onChange={event => updateValidation("allowedTypes", event.target.value)}
                        />
                    </label>
                </div>
            )}
        </section>
    );
}

export default FieldConfigurationPanel;