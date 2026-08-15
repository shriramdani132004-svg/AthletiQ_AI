const API_BASE = "/api/v1/events";

async function request(path, options = {}) {

    const token =
        localStorage.getItem(
            "athletiq_access_token"
        );

    const response =
        await fetch(
            `${API_BASE}${path}`,
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

    if(!response.ok){

        const message =
            await response.text();

        throw new Error(
            message ||
            `Application request failed: ${response.status}`
        );
    }

    return response.json();
}

export const applicationApi = {

    list: eventId =>
        request(
            `/${eventId}/applications`
        )
};