const API_BASE = "/api/v1/events";

async function request(path, options = {}) {
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
            message || `Requirements request failed: ${response.status}`
        );
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export const requirementsApi = {
    get: (eventId) =>
        request(`/${eventId}/requirements`),

    update: (eventId, data) =>
        request(`/${eventId}/requirements`, {
            method: "PUT",
            body: JSON.stringify(data)
        })
};