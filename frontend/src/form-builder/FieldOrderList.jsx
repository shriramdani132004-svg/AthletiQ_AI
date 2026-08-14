import { useMemo, useState } from "react";
import { moveField, normalizeFieldOrder, sortFields } from "./fieldOrderUtils";

function FieldOrderList({
    fields = [],
    editable = false,
    onOrderChange
}) {
    const [draggedIndex, setDraggedIndex] = useState(null);

    const orderedFields = useMemo(
        () => sortFields(fields),
        [fields]
    );

    function handleDragStart(index) {
        if (!editable) {
            return;
        }

        setDraggedIndex(index);
    }

    function handleDrop(targetIndex) {
        if (!editable || draggedIndex === null) {
            return;
        }

        if (draggedIndex === targetIndex) {
            setDraggedIndex(null);
            return;
        }

        const reordered = moveField(
            orderedFields,
            draggedIndex,
            targetIndex
        );

        onOrderChange(normalizeFieldOrder(reordered));
        setDraggedIndex(null);
    }

    return (
        <section>
            <h3>Field Order</h3>

            {orderedFields.length === 0 ? (
                <p>No fields to reorder.</p>
            ) : (
                <div>
                    {orderedFields.map((field, index) => (
                        <article
                            key={field.id ?? field.fieldKey ?? index}
                            draggable={editable}
                            onDragStart={() => handleDragStart(index)}
                            onDragOver={event => {
                                if (editable) {
                                    event.preventDefault();
                                }
                            }}
                            onDrop={() => handleDrop(index)}
                        >
                            <strong>
                                {index + 1}. {field.label || field.fieldKey}
                            </strong>

                            <span>
                                {" "}{field.fieldType}
                            </span>

                            {editable && (
                                <span>
                                    {" "}Drag to reorder
                                </span>
                            )}
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}

export default FieldOrderList;