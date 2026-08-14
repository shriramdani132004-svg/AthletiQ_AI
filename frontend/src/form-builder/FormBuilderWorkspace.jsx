import { useMemo } from "react";
import FieldConfigurationPanel from "./FieldConfigurationPanel";
import FieldOrderList from "./FieldOrderList";
import FormPreview from "./FormPreview";
import FormFieldCard from "./FormFieldCard";
import { validateFieldConfiguration } from "./fieldValidation";
import { normalizeFieldOrder } from "./fieldOrderUtils";

function FormBuilderWorkspace({
    fields = [],
    selectedField = null,
    editable = true,
    onFieldChange,
    onOrderChange,
    onEditField,
    onDuplicateField,
    onDeleteField
}) {
    const orderedFields = useMemo(
        () => normalizeFieldOrder(fields),
        [fields]
    );

    const selectedErrors = selectedField
        ? validateFieldConfiguration(selectedField)
        : [];

    return (
        <div className="form-builder-workspace">
            <section className="form-builder-fields">
                <header>
                    <h2>Form Fields</h2>
                    <p>Build and configure the application form.</p>
                </header>

                {orderedFields.length === 0 ? (
                    <p>No fields have been added yet.</p>
                ) : (
                    orderedFields.map((field, index) => (
                        <FormFieldCard
                            key={field.id ?? field.fieldKey ?? index}
                            field={field}
                            index={index}
                            onEdit={onEditField}
                            onDuplicate={onDuplicateField}
                            onDelete={onDeleteField}
                        />
                    ))
                )}

                <FieldOrderList
                    fields={orderedFields}
                    editable={editable}
                    onOrderChange={onOrderChange}
                />
            </section>

            <section className="form-builder-configuration">
                {selectedField ? (
                    <>
                        <FieldConfigurationPanel
                            field={selectedField}
                            onChange={onFieldChange}
                        />

                        {selectedErrors.length > 0 && (
                            <div role="alert">
                                <h3>Configuration Issues</h3>
                                <ul>
                                    {selectedErrors.map(error => (
                                        <li key={error}>{error}</li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </>
                ) : (
                    <p>Select a field to configure it.</p>
                )}
            </section>

            <section className="form-builder-preview">
                <FormPreview fields={orderedFields} />
            </section>
        </div>
    );
}

export default FormBuilderWorkspace;