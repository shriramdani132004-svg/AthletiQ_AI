const API_BASE = "/api/v1/events";

async function request(path = "", options = {}) {
    const token = localStorage.getItem("athletiq_access_token");

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {})
        }
    });

    if (!response.ok) {
        const message = await response.text();
        throw new Error(
            message || `Event request failed: ${response.status}`
        );
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export const eventApi = {
    list: () => request(),

    get: (eventId) =>
        request(`/${eventId}`),

    create: (data) =>
        request("", {
            method: "POST",
            body: JSON.stringify(data)
        }),

    update: (eventId, data) =>
        request(`/${eventId}`, {
            method: "PUT",
            body: JSON.stringify(data)
        }),

    publish: (eventId) =>
        request(`/${eventId}/publish`, {
            method: "POST"
        }),

    openApplications: (eventId) =>
        request(`/${eventId}/applications/open`, {
            method: "POST"
        }),

    pauseApplications: (eventId) =>
        request(`/${eventId}/applications/pause`, {
            method: "POST"
        }),

    reopenApplications: (eventId) =>
        request(`/${eventId}/applications/reopen`, {
            method: "POST"
        }),

    closeApplications: (eventId) =>
        request(`/${eventId}/applications/close`, {
            method: "POST"
        }),

    archive: (eventId) =>
        request(`/${eventId}/archive`, {
            method: "POST"
        }),

    duplicate: (eventId) =>
        request(`/${eventId}/duplicate`, {
            method: "POST"
        }),

    publicApplication: (eventId) =>
        request(
            `/${eventId}/public-application?publicBaseUrl=${encodeURIComponent(window.location.origin)}`,
            {
                method: "POST"
            }
        )
};