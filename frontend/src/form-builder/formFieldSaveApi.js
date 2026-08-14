import { updateFormField } from "../api/formApi";

export async function saveDraftFields({
    eventId,
    versionId,
    organizerId,
    fields
}) {
    if (!eventId) {
        throw new Error(Event ID is required.);
    }

    if (!versionId) {
        throw new Error(Form version ID is required.);
    }

    if (!organizerId) {
        throw new Error(Organizer ID is required.);
    }

    if (!Array.isArray(fields)) {
        throw new Error(Fields must be an array.);
    }

    const existingFields = fields.filter(
        (field) => field && field.id != null
    );

    const results = [];

    for (const field of existingFields) {
        const result = await updateFormField(
            eventId,
            versionId,
            field.id,
            organizerId,
            field
        );

        results.push(result);
    }

    return {
        savedCount: results.length,
        results
    };
}