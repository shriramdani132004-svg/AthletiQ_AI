import { useEffect, useRef, useState } from "react";
import { createPortal } from "react-dom";
import { useNavigate } from "react-router-dom";
import { api } from "../api/apiClient";
import "./Register.css";

function AthletiQLogo() {
    return (
        <svg
            className="aq-register-logo"
            viewBox="0 0 72 72"
            aria-label="AthletiQ"
            role="img"
        >
            <defs>
                <linearGradient
                    id="aq-register-logo-gradient"
                    x1="10"
                    y1="10"
                    x2="62"
                    y2="62"
                    gradientUnits="userSpaceOnUse"
                >
                    <stop offset="0" stopColor="#38bdf8" />
                    <stop offset="0.5" stopColor="#0ea5e9" />
                    <stop offset="1" stopColor="#6366f1" />
                </linearGradient>
            </defs>

            <rect
                x="2"
                y="2"
                width="68"
                height="68"
                rx="20"
                fill="#071a33"
            />

            <path
                d="M36 10L56 18V34C56 46 48 55 36 62C24 55 16 46 16 34V18L36 10Z"
                fill="rgba(255,255,255,.03)"
                stroke="url(#aq-register-logo-gradient)"
                strokeWidth="2.5"
            />

            <path
                d="M24 47L32.5 26H39.5L48 47H42L40.1 41.8H31.9L30 47H24ZM33.8 36.5H38.2L36 30.2L33.8 36.5Z"
                fill="#e0f2fe"
            />

            <circle
                cx="55"
                cy="17"
                r="3.5"
                fill="#38bdf8"
            />
        </svg>
    );
}

function EyeIcon({ visible }) {
    if (visible) {
        return (
            <svg
                viewBox="0 0 24 24"
                className="aq-register-icon"
                aria-hidden="true"
            >
                <path d="M2.5 12s3.2-6 9.5-6 9.5 6 9.5 6-3.2 6-9.5 6-9.5-6-9.5-6Z" />
                <circle cx="12" cy="12" r="2.8" />
            </svg>
        );
    }

    return (
        <svg
            viewBox="0 0 24 24"
            className="aq-register-icon"
            aria-hidden="true"
        >
            <path d="M3 3l18 18" />
            <path d="M10.6 10.6a2 2 0 1 0 2.8 2.8" />
            <path d="M9.7 5.2A10.6 10.6 0 0 1 12 5c6.2 0 9.5 7 9.5 7a16.5 16.5 0 0 1-3.2 4.1" />
            <path d="M6.1 6.1C3.8 8 2.5 12 2.5 12s3.2 6 9.5 6c1.3 0 2.5-.3 3.5-.7" />
        </svg>
    );
}

function ArrowIcon() {
    return (
        <svg
            viewBox="0 0 24 24"
            className="aq-register-arrow"
            aria-hidden="true"
        >
            <path d="M5 12h13" />
            <path d="M13 6l6 6-6 6" />
        </svg>
    );
}

function CheckIcon() {
    return (
        <svg
            viewBox="0 0 24 24"
            className="aq-register-check"
            aria-hidden="true"
        >
            <path d="M5 12.5l4.2 4.2L19 7" />
        </svg>
    );
}

function LockIcon() {
    return (
        <svg
            viewBox="0 0 24 24"
            className="aq-register-lock"
            aria-hidden="true"
        >
            <rect
                x="4"
                y="10"
                width="16"
                height="11"
                rx="2"
            />
            <path d="M8 10V7a4 4 0 0 1 8 0v3" />
        </svg>
    );
}

export default function Register() {
    const navigate = useNavigate();
    const rootRef = useRef(null);

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");

    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const [mounted, setMounted] = useState(false);

    useEffect(() => {
        setMounted(true);

        const previousOverflow =
            document.body.style.overflow;

        document.body.style.overflow = "hidden";

        return () => {
            document.body.style.overflow =
                previousOverflow;
        };
    }, []);

    function handleMouseMove(event) {
        if (!rootRef.current) {
            return;
        }

        rootRef.current.style.setProperty(
            "--mouse-x",
            `${event.clientX}px`
        );

        rootRef.current.style.setProperty(
            "--mouse-y",
            `${event.clientY}px`
        );
    }

    function clearError() {
        if (error) {
            setError("");
        }
    }

    async function submit(event) {
        event.preventDefault();

        setError("");
        setSuccess(false);
        setLoading(true);

        try {
            await api.post(
                "/v1/auth/register",
                {
                    email,
                    password,
                    firstName,
                    lastName
                }
            );

            setSuccess(true);
        } catch (exception) {
            setError(
                exception?.message ||
                "Registration failed."
            );
        } finally {
            setLoading(false);
        }
    }

    const passwordLength = password.length;

    const passwordStrength =
        passwordLength === 0
            ? 0
            : passwordLength < 6
                ? 1
                : passwordLength < 10
                    ? 2
                    : 3;

    const content = (
        <div
            className="aq-register-viewport"
            ref={rootRef}
            onMouseMove={handleMouseMove}
        >
            {/* =================================================
                BACKGROUND
                ================================================= */}

            <div className="aq-register-spotlight" />

            <div className="aq-register-aurora">
                <div className="aq-register-orb aq-register-orb-one" />
                <div className="aq-register-orb aq-register-orb-two" />
                <div className="aq-register-orb aq-register-orb-three" />
            </div>

            <div className="aq-register-grid" />

            {/* =================================================
                MAIN LAYOUT
                ================================================= */}

            <div className="aq-register-layout">

                {/* BRAND COLUMN */}
                <section className="aq-register-brand-column">

                    <div className="aq-register-brand-header">

                        <div className="aq-register-logo-box">
                            <AthletiQLogo />
                        </div>

                        <div>
                            <div className="aq-register-brand-title">
                                AthletiQ
                                <span className="aq-register-pro-chip">
                                    PRO
                                </span>
                            </div>

                            <div className="aq-register-brand-sub">
                                SPORTS TALENT INTELLIGENCE
                            </div>
                        </div>

                    </div>

                    <div className="aq-register-brand-center">

                        <div className="aq-register-badge">
                            <span className="aq-register-badge-dot" />
                            ORGANIZER ONBOARDING v2.6
                        </div>

                        <h1>
                            Build your
                            <span className="aq-register-gradient-text">
                                recruitment command center.
                            </span>
                        </h1>

                        <p className="aq-register-hero-description">
                            Create your organizer identity and bring
                            events, applicant intelligence and
                            evaluation workflows into one synchronized
                            sports recruitment workspace.
                        </p>

                        <div className="aq-register-feature-grid">

                            <div className="aq-register-feature">
                                <strong>
                                    01
                                </strong>

                                <span>
                                    EVENT CREATION
                                </span>

                                <small>
                                    Build structured recruitment events.
                                </small>
                            </div>

                            <div className="aq-register-feature">
                                <strong>
                                    02
                                </strong>

                                <span>
                                    TALENT PIPELINES
                                </span>

                                <small>
                                    Collect and organize player applications.
                                </small>
                            </div>

                            <div className="aq-register-feature">
                                <strong>
                                    03
                                </strong>

                                <span>
                                    SMART EVALUATION
                                </span>

                                <small>
                                    Evaluate candidates with clear criteria.
                                </small>
                            </div>

                        </div>

                        <div className="aq-register-progress">

                            <div className="aq-register-progress-label">
                                <span>
                                    ONBOARDING PROGRESS
                                </span>

                                <strong>
                                    01 / 03
                                </strong>
                            </div>

                            <div className="aq-register-progress-track">
                                <span />
                            </div>

                            <div className="aq-register-progress-caption">
                                Create your organizer account to continue.
                            </div>

                        </div>

                    </div>

                    <div className="aq-register-brand-footer">

                        <div className="aq-register-pulse">
                            <span />
                            <i />
                        </div>

                        <span>
                            ATHLETIQ INTELLIGENCE ENGINE READY
                        </span>

                    </div>

                </section>

                {/* FORM COLUMN */}
                <section className="aq-register-form-column">

                    <div className="aq-register-card">

                        <div className="aq-register-card-header">

                            <span className="aq-register-access-tag">
                                CREATE ORGANIZER ACCOUNT
                            </span>

                            <h2>
                                Get started
                            </h2>

                            <p>
                                Set up your workspace in a few seconds.
                            </p>

                        </div>

                        {success ? (
                            <div
                                className="aq-register-success"
                                role="status"
                                aria-live="polite"
                            >

                                <div className="aq-register-success-icon">
                                    <CheckIcon />
                                </div>

                                <span className="aq-register-success-tag">
                                    ACCOUNT CREATED
                                </span>

                                <h3>
                                    Welcome to AthletiQ.
                                </h3>

                                <p>
                                    Your organizer account was created
                                    successfully. Verify your email before
                                    signing in.
                                </p>

                                <button
                                    type="button"
                                    className="aq-register-primary-button"
                                    onClick={() =>
                                        navigate("/login")
                                    }
                                >
                                    <span>
                                        GO TO LOGIN
                                    </span>

                                    <ArrowIcon />
                                </button>

                            </div>
                        ) : (
                            <>
                                {error && (
                                    <div
                                        className="aq-register-error"
                                        role="alert"
                                        aria-live="polite"
                                    >
                                        <span className="aq-register-error-icon">
                                            !
                                        </span>

                                        <div>
                                            <strong>
                                                Registration failed
                                            </strong>

                                            <span>
                                                {error}
                                            </span>
                                        </div>
                                    </div>
                                )}

                                <form
                                    className="aq-register-form"
                                    onSubmit={submit}
                                >

                                    <div className="aq-register-name-row">

                                        <div className="aq-register-input-group">
                                            <label htmlFor="aq-register-first-name">
                                                FIRST NAME
                                            </label>

                                            <input
                                                id="aq-register-first-name"
                                                name="firstName"
                                                value={firstName}
                                                onChange={(event) => {
                                                    setFirstName(
                                                        event.target.value
                                                    );
                                                    clearError();
                                                }}
                                                placeholder="First name"
                                                autoComplete="given-name"
                                                required
                                                disabled={loading}
                                            />
                                        </div>

                                        <div className="aq-register-input-group">
                                            <label htmlFor="aq-register-last-name">
                                                LAST NAME
                                            </label>

                                            <input
                                                id="aq-register-last-name"
                                                name="lastName"
                                                value={lastName}
                                                onChange={(event) => {
                                                    setLastName(
                                                        event.target.value
                                                    );
                                                    clearError();
                                                }}
                                                placeholder="Last name"
                                                autoComplete="family-name"
                                                required
                                                disabled={loading}
                                            />
                                        </div>

                                    </div>

                                    <div className="aq-register-input-group">

                                        <label htmlFor="aq-register-email">
                                            EMAIL ADDRESS
                                        </label>

                                        <div className="aq-register-input-box">

                                            <span className="aq-register-prefix">
                                                @
                                            </span>

                                            <input
                                                id="aq-register-email"
                                                name="email"
                                                type="email"
                                                value={email}
                                                onChange={(event) => {
                                                    setEmail(
                                                        event.target.value
                                                    );
                                                    clearError();
                                                }}
                                                placeholder="organizer@athletiq.ai"
                                                autoComplete="email"
                                                required
                                                disabled={loading}
                                            />

                                        </div>

                                    </div>

                                    <div className="aq-register-input-group">

                                        <label htmlFor="aq-register-password">
                                            PASSWORD
                                        </label>

                                        <div className="aq-register-input-box">

                                            <span className="aq-register-prefix aq-register-lock-prefix">
                                                <LockIcon />
                                            </span>

                                            <input
                                                id="aq-register-password"
                                                name="password"
                                                type={
                                                    showPassword
                                                        ? "text"
                                                        : "password"
                                                }
                                                value={password}
                                                onChange={(event) => {
                                                    setPassword(
                                                        event.target.value
                                                    );
                                                    clearError();
                                                }}
                                                placeholder="Create a secure password"
                                                autoComplete="new-password"
                                                required
                                                disabled={loading}
                                            />

                                            <button
                                                type="button"
                                                className="aq-register-eye-button"
                                                onClick={() =>
                                                    setShowPassword(
                                                        current =>
                                                            !current
                                                    )
                                                }
                                                disabled={loading}
                                                aria-label={
                                                    showPassword
                                                        ? "Hide password"
                                                        : "Show password"
                                                }
                                            >
                                                <EyeIcon
                                                    visible={
                                                        showPassword
                                                    }
                                                />
                                            </button>

                                        </div>

                                        <div className="aq-register-password-meter">

                                            <div className="aq-register-password-bars">
                                                <span className={
                                                    passwordStrength >= 1
                                                        ? "active weak"
                                                        : ""
                                                } />

                                                <span className={
                                                    passwordStrength >= 2
                                                        ? "active medium"
                                                        : ""
                                                } />

                                                <span className={
                                                    passwordStrength >= 3
                                                        ? "active strong"
                                                        : ""
                                                } />
                                            </div>

                                            <span>
                                                {passwordLength === 0
                                                    ? "Use at least 8 characters"
                                                    : passwordLength < 6
                                                        ? "Too short"
                                                        : passwordLength < 10
                                                            ? "Good start"
                                                            : "Strong password"}
                                            </span>

                                        </div>

                                    </div>

                                    <div className="aq-register-security-note">

                                        <span>
                                            <LockIcon />
                                        </span>

                                        <p>
                                            Your account requires email
                                            verification before first sign in.
                                        </p>

                                    </div>

                                    <button
                                        type="submit"
                                        className="aq-register-primary-button"
                                        disabled={loading}
                                    >

                                        <span className="aq-register-button-shimmer" />

                                        {loading ? (
                                            <>
                                                <span className="aq-register-loader" />
                                                <span>
                                                    CREATING ACCOUNT...
                                                </span>
                                            </>
                                        ) : (
                                            <>
                                                <span>
                                                    CREATE ORGANIZER ACCOUNT
                                                </span>

                                                <ArrowIcon />
                                            </>
                                        )}

                                    </button>

                                </form>
                            </>
                        )}

                        {!success && (
                            <div className="aq-register-bottom-actions">

                                <span>
                                    Already have an organizer account?
                                </span>

                                <button
                                    type="button"
                                    onClick={() =>
                                        navigate("/login")
                                    }
                                    disabled={loading}
                                >
                                    SIGN IN
                                </button>

                            </div>
                        )}

                        <div className="aq-register-encryption">

                            <span className="aq-register-encryption-dot" />

                            SECURE ORGANIZER ACCESS

                        </div>

                    </div>

                </section>

            </div>
        </div>
    );

    return mounted
        ? createPortal(content, document.body)
        : null;
}
