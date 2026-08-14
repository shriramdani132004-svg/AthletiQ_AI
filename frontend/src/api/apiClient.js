const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

async function request(path, options = {}) {
    const token = localStorage.getItem("athletiq_access_token");
    const headers = { "Content-Type": "application/json", ...options.headers };
    if (token) { headers.Authorization = `Bearer ${token}`; }
    const response = await fetch(`${API_BASEURL}${path}`, { ...options, headers });
    if (!response.ok) { const body = await response.text(); const error = new Error(body || `Request failed with status ${response.status}`); error.status = response.status; throw error; }
    if (response.status === 204) return null;
    return response.json();
}

export const api = {
    get(path) { return request(path); },
    post(path, body) { return request(path, { method: "POST", body: JSON.stringify(body) }); },
    put(path, body) { return request(path, { method: "PUT", body: JSON.stringify(body) }); },
    delete(path) { return request(path, { method: "DELETE" }); }
};
