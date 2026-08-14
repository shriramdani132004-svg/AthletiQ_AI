export async function updateFieldOrder(
    eventId,
    versionId,
    organizerId,
    fields,
    updateFormField
) {
    const ordered = [...fields].sort(
        (a, b) => Number(a.displayOrder ?? 0) - Number(b.displayOrder ?? 0)
    );

    const updated = [];

    for (let index = 0; index < ordered.length; index += 1) {
        const field = ordered[index];

        const nextField = {
            ...field,
            displayOrder: index
        };

        const result = await updateFormField(
            eventId,
            versionId,
            field.id,
            organizerId,
            nextField
        );

        updated.push(result);
    }

    return updated;
}