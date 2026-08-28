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

    if(response.status === 204){
        return null;
    }

    return response.json();
}

function buildQuery(params = {}){

    const query =
        new URLSearchParams();

    Object.entries(params)
        .forEach(([key,value]) => {

            if(
                value !== undefined &&
                value !== null &&
                String(value).trim() !== ""
            ){

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
        )
};