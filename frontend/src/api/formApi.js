const API_BASE = "/api";

async function request(url, options = {}) {
    const response = await fetch(`${API_BASE}${url}`, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        let message = `Request failed with status ${response.status}`;

        try {
            const body = await response.json();

            if (body?.message) {
                message = body.message;
            }
        } catch {
            // Response may not contain JSON.
        }

        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

/* =========================================================
   FORM
   ========================================================= */

export async function createForm(eventId, organizerId) {
    return request(
        `/events/${eventId}/form?organizerId=${organizerId}`,
        {
            method: "POST"
        }
    );
}

export async function getForm(eventId, organizerId) {
    return request(
        `/events/${eventId}/form?organizerId=${organizerId}`
    );
}

/* =========================================================
   FORM VERSIONS
   ========================================================= */

export async function getFormVersions(
    eventId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions?organizerId=${organizerId}`
    );
}

export async function createFormVersion(
    eventId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions?organizerId=${organizerId}`,
        {
            method: "POST"
        }
    );
}

export async function cloneFormVersion(
    eventId,
    versionId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/clone?organizerId=${organizerId}`,
        {
            method: "POST"
        }
    );
}

export async function publishFormVersion(
    eventId,
    versionId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/publish?organizerId=${organizerId}`,
        {
            method: "POST"
        }
    );
}

/* =========================================================
   FORM FIELDS
   ========================================================= */

export async function getFormFields(
    eventId,
    versionId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields?organizerId=${organizerId}`
    );
}

export async function getFormField(
    eventId,
    versionId,
    fieldId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields/${fieldId}?organizerId=${organizerId}`
    );
}

export async function addFormField(
    eventId,
    versionId,
    organizerId,
    field
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields?organizerId=${organizerId}`,
        {
            method: "POST",
            body: JSON.stringify(field)
        }
    );
}

export async function updateFormField(
    eventId,
    versionId,
    fieldId,
    organizerId,
    field
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields/${fieldId}?organizerId=${organizerId}`,
        {
            method: "PUT",
            body: JSON.stringify(field)
        }
    );
}

export async function deleteFormField(
    eventId,
    versionId,
    fieldId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields/${fieldId}?organizerId=${organizerId}`,
        {
            method: "DELETE"
        }
    );
}

export async function duplicateFormField(
    eventId,
    versionId,
    fieldId,
    organizerId
) {
    return request(
        `/events/${eventId}/form/versions/${versionId}/fields/${fieldId}/duplicate?organizerId=${organizerId}`,
        {
            method: "POST"
        }
    );
}

/* =========================================================
   FIELD TYPES
   ========================================================= */

export const FIELD_TYPES = Object.freeze([
    {
        value: "TEXT",
        label: "Text"
    },
    {
        value: "NUMBER",
        label: "Number"
    },
    {
        value: "EMAIL",
        label: "Email"
    },
    {
        value: "PHONE",
        label: "Phone"
    },
    {
        value: "DATE",
        label: "Date"
    },
    {
        value: "DROPDOWN",
        label: "Dropdown"
    },
    {
        value: "RADIO",
        label: "Radio Buttons"
    },
    {
        value: "CHECKBOX",
        label: "Checkboxes"
    },
    {
        value: "MULTI_SELECT",
        label: "Multi-select"
    },
    {
        value: "RATING",
        label: "Rating"
    },
    {
        value: "FILE",
        label: "File Upload"
    },
    {
        value: "IMAGE",
        label: "Image Upload"
    },
    {
        value: "URL",
        label: "URL"
    },
    {
        value: "LONG_TEXT",
        label: "Long Text"
    }
]);

export function createEmptyField(displayOrder = 0) {
    return {
        fieldKey: "",
        fieldType: "TEXT",
        label: "",
        description: "",
        displayOrder,
        required: false,
        validationConfig: "",
        optionsConfig: ""
    };
}