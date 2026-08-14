function FormFieldCard({
    field,
    editable,
    onEdit,
    onDuplicate,
    onDelete
}) {
    return (
        <article>

            <div>
                <strong>
                    {field.label}
                </strong>

                <span>
                    {" "}
                    {field.fieldType}
                </span>
            </div>

            {field.description && (
                <p>
                    {field.description}
                </p>
            )}

            <small>
                Key: {field.fieldKey}
            </small>

            <small>
                {" "}
                Order: {field.displayOrder}
            </small>

            {field.required && (
                <small>
                    {" "}
                    Required
                </small>
            )}

            {editable && (
                <div>

                    <button
                        type="button"
                        onClick={() =>
                            onEdit(field)
                        }
                    >
                        Edit
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            onDuplicate(field.id)
                        }
                    >
                        Duplicate
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            onDelete(field.id)
                        }
                    >
                        Delete
                    </button>

                </div>
            )}

        </article>
    );
}

export default FormFieldCard;