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
            message || `Evaluation criteria request failed: ${response.status}`
        );
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export const evaluationCriteriaApi = {
    list: (eventId) =>
        request(`/${eventId}/evaluation-criteria`),

    create: (eventId, data) =>
        request(`/${eventId}/evaluation-criteria`, {
            method: "POST",
            body: JSON.stringify(data)
        }),

    update: (eventId, criterionId, data) =>
        request(
            `/${eventId}/evaluation-criteria/${criterionId}`,
            {
                method: "PUT",
                body: JSON.stringify(data)
            }
        ),

    delete: (eventId, criterionId) =>
        request(
            `/${eventId}/evaluation-criteria/${criterionId}`,
            {
                method: "DELETE"
            }
        ),

    validation: (eventId) =>
        request(
            `/${eventId}/evaluation-criteria/validation`
        )
};