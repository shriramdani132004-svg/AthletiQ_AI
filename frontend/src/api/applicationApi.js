const API_BASE = "/api/v1/events";

async function request(path, options = {}) {
    return sendRequest(
        `${API_BASE}${path}`,
        options,
        "Application"
    );
}

async function requestDirect(path, options = {}) {
    return sendRequest(
        `/api/v1${path}`,
        options,
        "Selection"
    );
}

async function sendRequest(
    url,
    options,
    requestName
) {
    const token =
        localStorage.getItem(
            "athletiq_access_token"
        );

    const response =
        await fetch(
            url,
            {
                ...options,
                headers: {
                    "Content-Type":
                        "application/json",

                    ...(token
                        ? {
                            Authorization:
                                `Bearer ${token}`
                        }
                        : {}),

                    ...(options.headers || {})
                }
            }
        );

    if (!response.ok) {
        const message =
            await response.text();

        throw new Error(
            message ||
            `${requestName} request failed: ${response.status}`
        );
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

function buildQuery(params = {}) {
    const query =
        new URLSearchParams();

    Object.entries(params)
        .forEach(([key, value]) => {
            if (
                value !== undefined &&
                value !== null &&
                String(value).trim() !== ""
            ) {
                query.set(
                    key,
                    String(value)
                );
            }
        });

    const result =
        query.toString();

    return result
        ? `?${result}`
        : "";
}

export const applicationApi = {
    list: (
        eventId,
        {
            page = 0,
            size = 20,
            search = "",
            email = "",
            age = "",
            position = "",
            status = "",
            sort = "submittedAt",
            direction = "desc"
        } = {}
    ) =>
        request(
            `/${eventId}/applications` +
            buildQuery({
                page,
                size,
                search,
                email,
                age,
                position,
                status,
                sort,
                direction
            })
        ),

    statistics: eventId =>
        request(
            `/${eventId}/applications/statistics`
        ),

    get: (
        eventId,
        applicationId
    ) =>
        request(
            `/${eventId}/applications/${applicationId}`
        ),

    evaluateAI: (
        eventId,
        applicationId,
        organizerId
    ) =>
        request(
            `/${eventId}/applications/${applicationId}/ai-evaluation` +
            `?organizerId=${encodeURIComponent(organizerId)}`,
            {
                method: "POST"
            }
        ),

        decideSelection: (
        applicationId,
        selectionStatus,
        selectionReason = ""
    ) =>
        requestDirect(
            `/organizer/applications/${applicationId}/selection`,
            {
                method: "POST",
                body: JSON.stringify({
                    selectionStatus,
                    selectionReason
                })
            }
        ),

    sendSelectionEmail: (
        applicationId,
        subject,
        message
    ) =>
        requestDirect(
            `/organizer/applications/${applicationId}/selection-email`,
            {
                method: "POST",
                body: JSON.stringify({
                    subject,
                    message
                })
            }
        ),
        getSelectionEmailStatus: applicationId =>
        requestDirect(
            `/organizer/applications/${applicationId}/selection-email/status`
        )   
};