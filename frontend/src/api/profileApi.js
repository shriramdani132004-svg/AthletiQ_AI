const API_BASE = "/api/v1/profile";

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
        throw new Error(`Profile request failed: ${response.status}`);
    }
    if (response.status === 204) return null;
    return response.json();
}

export const profileApi = {
    getProfile: () => request(""),
    updateProfile: (data) => request("", { method: "PUT", body: JSON.stringify(data) }),
    changePassword: (data) => request("/password", { method: "PUT", body: JSON.stringify(data) }),
    getEmailPreferences: () => request("/email-preferences"),
    updateEmailPreferences: (data) => request("/email-preferences", { method: "PUT", body: JSON.stringify(data) }),
    getOrganization: () => request("/organization"),
    updateOrganization: (data) => request("/organization", { method: "PUT", body: JSON.stringify(data) }),
    getPhoto: () => request("/photo"),
    updatePhoto: (data) => request("/photo", { method: "PUT", body: JSON.stringify(data) })
};