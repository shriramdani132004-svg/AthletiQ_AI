import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/apiClient";

export default function Register() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        email: "",
        password: "",
        firstName: "",
        lastName: ""
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);

    function updateField(event) {
        setForm(current => ({
            ...current,
            [event.target.name]: event.target.value
        }));
    }

    async function submit(event) {
        event.preventDefault();
        setError("");
        setSuccess(false);
        setLoading(true);

        try {
            await api.post("/v1/auth/register", form);
            setSuccess(true);
        } catch (err) {
            setError(
                err?.message ||
                "Registration failed."
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <main>
            <h1>AthletiQ Registration</h1>

            {success && (
                <p role="status">
                    Registration successful. Please verify your email before login.
                </p>
            )}

            {error && (
                <p role="alert">
                    {error}
                </p>
            )}

            <form onSubmit={submit}>
                <label>
                    First Name
                    <input
                        name="firstName"
                        value={form.firstName}
                        onChange={updateField}
                        required
                    />
                </label>

                <label>
                    Last Name
                    <input
                        name="lastName"
                        value={form.lastName}
                        onChange={updateField}
                        required
                    />
                </label>

                <label>
                    Email
                    <input
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={updateField}
                        required
                    />
                </label>

                <label>
                    Password
                    <input
                        name="password"
                        type="password"
                        value={form.password}
                        onChange={updateField}
                        required
                    />
                </label>

                <button
                    type="submit"
                    disabled={loading}
                >
                    {loading ? "Registering..." : "Register"}
                </button>

                <button
                    type="button"
                    onClick={() => navigate("/login")}
                >
                    Back to Login
                </button>
            </form>
        </main>
    );
}