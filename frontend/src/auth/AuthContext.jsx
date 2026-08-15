import { createContext, useContext, useMemo, useState } from "react";
import { api } from "../api/apiClient";
import { clearTokens, isAccessToken, getAccessToken, getRefreshToken, saveTokens } from "./authStorage";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [accessToken, setAccessToken] = useState(getAccessToken());
    const [user, setUser] = useState(() => {
        const token = getAccessToken();
        if (!token) return null;

        try {
            const payload = JSON.parse(
                atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))
            );

            if (!payload?.sub || !payload?.email || !payload?.role) {
                return null;
            }

            return {
                userId: Number(payload.sub),
                email: payload.email,
                firstName: "",
                lastName: "",
                role: payload.role
            };
        } catch {
            return null;
        }
    });
    const [refreshToken, setRefreshToken] = useState(getRefreshToken());

    async function login(email, password) {
        const response = await api.post("/v1/auth/login", { email, password });
        saveTokens(response.accessToken, response.refreshToken); setAccessToken(response.accessToken); setRefreshToken(response.refreshToken);
        setUser({ userId: response.userId, email: response.email, firstName: response.firstName, lastName: response.lastName, role: response.role }); return response;
    }
    async function refreshAccessToken() {
        if (!refreshToken) throw new Error("No refresh token available");
        const response = await api.post("/v1/auth/refresh", { refreshToken }); saveTokens(response.accessToken, refreshToken); setAccessToken(response.accessToken); return response.accessToken;
    }
    async function logout() {
        try { if (refreshToken) await api.post("/v1/auth/logout", { refreshToken }); } finally { clearTokens(); setAccessToken(null); setRefreshToken(null); setUser(null); }
    }
    const value = useMemo(() => ({accessToken,refreshToken,user,isAuthenticated:Boolean(accessToken),login,refreshAccessToken,logout}),[accessToken,refreshToken,user]);
    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used inside AuthProvider");
    return context;
}
