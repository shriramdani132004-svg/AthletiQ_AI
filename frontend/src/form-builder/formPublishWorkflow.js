import { publishFormVersion as publishFormVersionApi } from "../api/formApi";

export const FORM_PUBLISH_STATES = {
    DRAFT: "DRAFT",
    PUBLISHED: "PUBLISHED",
    ARCHIVED: "ARCHIVED"
};

export function canPublishVersion(version) {
    if (!version) return false;
    const status = String(version.status ?? version.formVersionStatus ?? "DRAFT").toUpperCase();
    return status === FORM_PUBLISH_STATES.DRAFT;
}

export function assertCanPublishVersion(version) {
    if (!canPublishVersion(version)) {
        throw new Error("Only DRAFT form versions can be published.");
    }
    return true;
}

export async function publishFormVersion({ eventId, version, organizerId }) {
    if (!eventId) throw new Error("Event ID is required.");
    if (!version?.id) throw new Error("Form version ID is required.");
    if (!organizerId) throw new Error("Organizer ID is required.");
    assertCanPublishVersion(version);
    return publishFormVersionApi(eventId, version.id, organizerId);
}