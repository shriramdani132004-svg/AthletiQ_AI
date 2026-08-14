function FormPreview({
    fields = []
}) {
    const orderedFields =
        fields
            .slice()
            .sort(
                (a, b) =>
                    a.displayOrder -
                    b.displayOrder
            );

    return (
        <section>

            <h2>
                Form Preview
            </h2>

            {orderedFields.length === 0 ? (

                <p>
                    Add fields to preview the form.
                </p>

            ) : (

                <form>

                    {orderedFields.map(field => (

                        <div key={field.id}>

                            <label>
                                {field.label}

                                {field.required && (
                                    <span>
                                        {" "}*
                                    </span>
                                )}
                            </label>

                            {field.fieldType === "LONG_TEXT" ? (

                                <textarea />

                            ) : field.fieldType === "DROPDOWN" ? (

                                <select>
                                    <option>
                                        Select an option
                                    </option>
                                </select>

                            ) : field.fieldType === "CHECKBOX" ? (

                                <input
                                    type="checkbox"
                                />

                            ) : field.fieldType === "RADIO" ? (

                                <input
                                    type="radio"
                                    name={
                                        field.fieldKey
                                    }
                                />

                            ) : field.fieldType === "FILE" ||
                              field.fieldType === "IMAGE" ? (

                                <input
                                    type="file"
                                />

                            ) : field.fieldType === "DATE" ? (

                                <input
                                    type="date"
                                />

                            ) : field.fieldType === "NUMBER" ? (

                                <input
                                    type="number"
                                />

                            ) : field.fieldType === "EMAIL" ? (

                                <input
                                    type="email"
                                />

                            ) : field.fieldType === "URL" ? (

                                <input
                                    type="url"
                                />

                            ) : field.fieldType === "PHONE" ? (

                                <input
                                    type="tel"
                                />

                            ) : (

                                <input
                                    type="text"
                                />
                            )}

                        </div>

                    ))}

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