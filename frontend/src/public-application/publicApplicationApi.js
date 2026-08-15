const API_BASE = "/api/public/apply";

async function request(publicCode, options = {}) {

    const response = await fetch(
        `${API_BASE}/${encodeURIComponent(publicCode)}`,
        {
            ...options,
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            }
        }
    );

    if (!response.ok) {
        const message = await response.text();

        throw new Error(
            message ||
            `Public application request failed: ${response.status}`
        );
    }

    return response.json();
}

export const publicApplicationApi = {

    get: (publicCode) =>
        request(publicCode),

    submit: (publicCode, answers) =>
        request(publicCode, {
            method: "POST",
            body: JSON.stringify({
                answers
            })
        })
};