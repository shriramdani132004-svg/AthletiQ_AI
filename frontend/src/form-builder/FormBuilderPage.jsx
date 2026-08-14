import { useEffect, useMemo, useState } from "react";
import FormVersionHistory from "./FormVersionHistory";
import {
    addFormField,
    createForm,
    createFormVersion,
    deleteFormField,
    duplicateFormField,
    getForm,
    getFormFields,
    getFormVersions,
    publishFormVersion,
    updateFormField
} from "../api/formApi";

import {
    FIELD_TYPES,
    createEmptyField
} from "../api/formApi";

function FormBuilderPage({
    eventId,
    organizerId
}) {
    const [form, setForm] = useState(null);
    const [versions, setVersions] = useState([]);
    const [fields, setFields] = useState([]);

    const [selectedVersionId, setSelectedVersionId] =
        useState(null);

    const [editingFieldId, setEditingFieldId] =
        useState(null);

    const [draftField, setDraftField] =
        useState(createEmptyField());

    const [loading, setLoading] =
        useState(true);

    const [saving, setSaving] =
        useState(false);

    const [error, setError] =
        useState("");

    const selectedVersion = useMemo(
        () =>
            versions.find(
                version =>
                    version.id === selectedVersionId
            ),
        [versions, selectedVersionId]
    );

    const isEditable =
        selectedVersion?.status === "DRAFT";

    async function loadForm() {

        setLoading(true);
        setError("");

        try {

            let currentForm;

            try {
                currentForm = await getForm(
                    eventId,
                    organizerId
                );
            } catch {

                currentForm = await createForm(
                    eventId,
                    organizerId
                );
            }

            setForm(currentForm);

            const currentVersions =
                await getFormVersions(
                    eventId,
                    organizerId
                );

            setVersions(currentVersions);

            const draft =
                currentVersions.find(
                    version =>
                        version.status === "DRAFT"
                );

            const selected =
                draft ||
                currentVersions[0];

            if (selected) {

                setSelectedVersionId(
                    selected.id
                );

                const currentFields =
                    await getFormFields(
                        eventId,
                        selected.id,
                        organizerId
                    );

                setFields(currentFields);
            }

        } catch (err) {

            setError(
                err.message ||
                "Unable to load form builder"
            );

        } finally {

            setLoading(false);
        }
    }

    useEffect(() => {

        if (eventId && organizerId) {
            loadForm();
        }

    }, [eventId, organizerId]);

    async function selectVersion(versionId) {

        setSelectedVersionId(versionId);
        setEditingFieldId(null);

        try {

            const currentFields =
                await getFormFields(
                    eventId,
                    versionId,
                    organizerId
                );

            setFields(currentFields);

        } catch (err) {

            setError(
                err.message ||
                "Unable to load fields"
            );
        }
    }

    async function createVersion() {

        setSaving(true);
        setError("");

        try {

            const version =
                await createFormVersion(
                    eventId,
                    organizerId
                );

            const updated =
                await getFormVersions(
                    eventId,
                    organizerId
                );

            setVersions(updated);
            setSelectedVersionId(version.id);
            setFields([]);

        } catch (err) {

            setError(
                err.message ||
                "Unable to create version"
            );

        } finally {

            setSaving(false);
        }
    }

    function startAddField() {

        setEditingFieldId(null);

        setDraftField(
            createEmptyField(fields.length)
        );
    }

    function startEditField(field) {

        setEditingFieldId(field.id);

        setDraftField({
            fieldKey: field.fieldKey || "",
            fieldType: field.fieldType || "TEXT",
            label: field.label || "",
            description: field.description || "",
            displayOrder:
                field.displayOrder ?? fields.length,
            required: Boolean(field.required),
            validationConfig:
                field.validationConfig || "",
            optionsConfig:
                field.optionsConfig || ""
        });
    }

    function updateDraft(name, value) {

        setDraftField(current => ({
            ...current,
            [name]: value
        }));
    }

    async function saveField() {

        if (!isEditable) {
            setError(
                "Only draft form versions can be modified."
            );
            return;
        }

        setSaving(true);
        setError("");

        try {

            if (editingFieldId) {

                await updateFormField(
                    eventId,
                    selectedVersionId,
                    editingFieldId,
                    organizerId,
                    draftField
                );

            } else {

                await addFormField(
                    eventId,
                    selectedVersionId,
                    organizerId,
                    draftField
                );
            }

            const updated =
                await getFormFields(
                    eventId,
                    selectedVersionId,
                    organizerId
                );

            setFields(updated);

            setEditingFieldId(null);
            setDraftField(
                createEmptyField(updated.length)
            );

        } catch (err) {

            setError(
                err.message ||
                "Unable to save field"
            );

        } finally {

            setSaving(false);
        }
    }

    async function removeField(fieldId) {

        if (!isEditable) {
            setError(
                "Only draft form versions can be modified."
            );
            return;
        }

        setSaving(true);
        setError("");

        try {

            await deleteFormField(
                eventId,
                selectedVersionId,
                fieldId,
                organizerId
            );

            const updated =
                await getFormFields(
                    eventId,
                    selectedVersionId,
                    organizerId
                );

            setFields(updated);

        } catch (err) {

            setError(
                err.message ||
                "Unable to delete field"
            );

        } finally {

            setSaving(false);
        }
    }

    async function duplicateField(fieldId) {

        if (!isEditable) {
            setError(
                "Only draft form versions can be modified."
            );
            return;
        }

        setSaving(true);
        setError("");

        try {

            await duplicateFormField(
                eventId,
                selectedVersionId,
                fieldId,
                organizerId
            );

            const updated =
                await getFormFields(
                    eventId,
                    selectedVersionId,
                    organizerId
                );

            setFields(updated);

        } catch (err) {

            setError(
                err.message ||
                "Unable to duplicate field"
            );

        } finally {

            setSaving(false);
        }
    }

    async function publishVersion() {

        if (!isEditable) {
            return;
        }

        setSaving(true);
        setError("");

        try {

            await publishFormVersion(
                eventId,
                selectedVersionId,
                organizerId
            );

            await loadForm();

        } catch (err) {

            setError(
                err.message ||
                "Unable to publish form"
            );

        } finally {

            setSaving(false);
        }
    }

    if (loading) {
        return (
            <div>
                Loading form builder...
            </div>
        );
    }

    return (
        <div className="form-builder-page">

            <header className="form-builder-header">

                <div>
                    <h1>
                        Form Builder
                    </h1>

                    <p>
                        Build the application form for this event.
                    </p>
                </div>

                <div>

                    <button
                        type="button"
                        onClick={createVersion}
                        disabled={saving}
                    >
                        New Version
                    </button>

                    <button
                        type="button"
                        onClick={publishVersion}
                        disabled={
                            saving ||
                            !isEditable ||
                            fields.length === 0
                        }
                    >
                        Publish Version
                    </button>

                </div>

            </header>

            {error && (
                <div role="alert">
                    {error}
                </div>
            )}

            <FormVersionHistory
                versions={versions}
                selectedVersionId={selectedVersionId}
                onSelect={selectVersion}
            />
            <section>

                <h2>
                    Form Versions
                </h2>

                <div>

                    {versions.map(version => (

                        <button
                            key={version.id}
                            type="button"
                            onClick={() =>
                                selectVersion(version.id)
                            }
                            aria-pressed={
                                version.id ===
                                selectedVersionId
                            }
                        >
                            Version{" "}
                            {version.versionNumber}
                            {" "}
                            ({version.status})
                        </button>

                    ))}

                </div>

            </section>

            <section>

                <div>

                    <h2>
                        Fields
                    </h2>

                    <button
                        type="button"
                        onClick={startAddField}
                        disabled={!isEditable || saving}
                    >
                        Add Field
                    </button>

                </div>

                {fields.length === 0 ? (

                    <p>
                        No fields yet. Add the first field
                        to start building the form.
                    </p>

                ) : (

                    <div>

                        {fields
                            .slice()
                            .sort(
                                (a, b) =>
                                    a.displayOrder -
                                    b.displayOrder
                            )
                            .map(field => (

                                <article
                                    key={field.id}
                                >

                                    <div>

                                        <strong>
                                            {field.label}
                                        </strong>

                                        <span>
                                            {" "}
                                            {field.fieldType}
                                        </span>

                                        {field.required && (
                                            <span>
                                                {" "}
                                                Required
                                            </span>
                                        )}

                                    </div>

                                    {field.description && (
                                        <p>
                                            {
                                                field.description
                                            }
                                        </p>
                                    )}

                                    <div>

                                        <button
                                            type="button"
                                            onClick={() =>
                                                startEditField(
                                                    field
                                                )
                                            }
                                            disabled={
                                                !isEditable ||
                                                saving
                                            }
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            onClick={() =>
                                                duplicateField(
                                                    field.id
                                                )
                                            }
                                            disabled={
                                                !isEditable ||
                                                saving
                                            }
                                        >
                                            Duplicate
                                        </button>

                                        <button
                                            type="button"
                                            onClick={() =>
                                                removeField(
                                                    field.id
                                                )
                                            }
                                            disabled={
                                                !isEditable ||
                                                saving
                                            }
                                        >
                                            Delete
                                        </button>

                                    </div>

                                </article>

                            ))}

                    </div>
                )}

            </section>

            {isEditable && (
                <section>

                    <h2>
                        {editingFieldId
                            ? "Edit Field"
                            : "Add Field"}
                    </h2>

                    <div>

                        <label>
                            Field Key

                            <input
                                value={
                                    draftField.fieldKey
                                }
                                onChange={event =>
                                    updateDraft(
                                        "fieldKey",
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <label>
                            Field Type

                            <select
                                value={
                                    draftField.fieldType
                                }
                                onChange={event =>
                                    updateDraft(
                                        "fieldType",
                                        event.target.value
                                    )
                                }
                            >

                                {FIELD_TYPES.map(type => (

                                    <option
                                        key={type.value}
                                        value={type.value}
                                    >
                                        {type.label}
                                    </option>

                                ))}

                            </select>
                        </label>

                        <label>
                            Label

                            <input
                                value={
                                    draftField.label
                                }
                                onChange={event =>
                                    updateDraft(
                                        "label",
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <label>
                            Description

                            <textarea
                                value={
                                    draftField.description
                                }
                                onChange={event =>
                                    updateDraft(
                                        "description",
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <label>
                            Display Order

                            <input
                                type="number"
                                min="0"
                                value={
                                    draftField.displayOrder
                                }
                                onChange={event =>
                                    updateDraft(
                                        "displayOrder",
                                        Number(
                                            event.target.value
                                        )
                                    )
                                }
                            />
                        </label>

                        <label>
                            <input
                                type="checkbox"
                                checked={
                                    draftField.required
                                }
                                onChange={event =>
                                    updateDraft(
                                        "required",
                                        event.target.checked
                                    )
                                }
                            />

                            Required
                        </label>

                        <label>
                            Validation Configuration

                            <textarea
                                value={
                                    draftField.validationConfig
                                }
                                onChange={event =>
                                    updateDraft(
                                        "validationConfig",
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <label>
                            Options Configuration

                            <textarea
                                value={
                                    draftField.optionsConfig
                                }
                                onChange={event =>
                                    updateDraft(
                                        "optionsConfig",
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <button
                            type="button"
                            onClick={saveField}
                            disabled={saving}
                        >
                            {editingFieldId
                                ? "Update Field"
                                : "Add Field"}
                        </button>

                        <button
                            type="button"
                            onClick={() => {
                                setEditingFieldId(null);
                                setDraftField(
                                    createEmptyField(
                                        fields.length
                                    )
                                );
                            }}
                            disabled={saving}
                        >
                            Cancel
                        </button>

                    </div>

                </section>
            )}

        </div>
    );
}

export default FormBuilderPage;