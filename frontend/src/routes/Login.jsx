import { useState, useEffect, useRef } from "react";
import { createPortal } from "react-dom";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import "./Login.css";

function AthletiQLogo() {
    return (
        <svg
            className="aq-login-logo"
            viewBox="0 0 72 72"
            aria-label="AthletiQ"
            role="img"
        >
            <defs>
                <linearGradient
                    id="aq-login-logo-gradient"
                    x1="10"
                    y1="10"
                    x2="62"
                    y2="62"
                    gradientUnits="userSpaceOnUse"
                >
                    <stop offset="0%" stopColor="#38bdf8" />
                    <stop offset="50%" stopColor="#0ea5e9" />
                    <stop offset="100%" stopColor="#6366f1" />
                </linearGradient>
            </defs>

            <rect x="2" y="2" width="68" height="68" rx="20" fill="#071a33" />
            <path
                d="M36 10L56 18V34C56 46 48 55 36 62C24 55 16 46 16 34V18L36 10Z"
                fill="rgba(255,255,255,.03)"
                stroke="url(#aq-login-logo-gradient)"
                strokeWidth="2.5"
            />
            <path
                d="M24 47L32.5 26H39.5L48 47H42L40.1 41.8H31.9L30 47H24ZM33.8 36.5H38.2L36 30.2L33.8 36.5Z"
                fill="#e0f2fe"
            />
            <circle cx="55" cy="17" r="3.5" fill="#38bdf8" />
        </svg>
    );
}

function EyeIcon({ visible }) {
    return visible ? (
        <svg viewBox="0 0 24 24" className="aq-login-icon" aria-hidden="true">
            <path d="M2.5 12s3.2-6 9.5-6 9.5 6 9.5 6-3.2 6-9.5 6-9.5-6Z" />
            <circle cx="12" cy="12" r="2.8" />
        </svg>
    ) : (
        <svg viewBox="0 0 24 24" className="aq-login-icon" aria-hidden="true">
            <path d="M3 3l18 18" />
            <path d="M10.6 10.6a2 2 0 1 0 2.8 2.8" />
            <path d="M9.7 5.2A10.6 10.6 0 0 1 12 5c6.2 0 9.5 7 9.5 7a16.5 16.5 0 0 1-3.2 4.1" />
            <path d="M6.1 6.1C3.8 8 2.5 12 2.5 12s3.2 6 9.5 6c1.3 0 2.5-.3 3.5-.7" />
        </svg>
    );
}

function ArrowIcon() {
    return (
        <svg viewBox="0 0 24 24" className="aq-login-arrow" aria-hidden="true">
            <path d="M5 12h13" />
            <path d="M13 6l6 6-6 6" />
        </svg>
    );
}

export default function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [mounted, setMounted] = useState(false);

    const rootRef = useRef(null);

    useEffect(() => {
        setMounted(true);
        // Lock body scrolling while login is active
        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = "";
        };
    }, []);

    function handleMouseMove(e) {
        if (!rootRef.current) return;
        rootRef.current.style.setProperty("--mouse-x", `${e.clientX}px`);
        rootRef.current.style.setProperty("--mouse-y", `${e.clientY}px`);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            await login(email, password);
            navigate("/", { replace: true });
        } catch (exception) {
            setError(
                exception?.message ||
                "Unable to sign in. Please check your credentials."
            );
        } finally {
            setLoading(false);
        }
    }

    const content = (
        <div
            className="aq-direct-viewport-root"
            ref={rootRef}
            onMouseMove={handleMouseMove}
        >
            {/* Interactive Spotlight and Mesh Aurora */}
            <div className="aq-spotlight" />
            <div className="aq-bg-aurora">
                <div className="aq-orb aq-orb-1" />
                <div className="aq-orb aq-orb-2" />
                <div className="aq-orb aq-orb-3" />
            </div>
            <div className="aq-cyber-grid" />

            <div className="aq-portal-layout">
                {/* Brand Experience Column */}
                <section className="aq-brand-col">
                    <div className="aq-brand-header">
                        <div className="aq-logo-box">
                            <AthletiQLogo />
                        </div>
                        <div>
                            <div className="aq-brand-title">
                                AthletiQ <span className="aq-chip">PRO</span>
                            </div>
                            <div className="aq-brand-sub">SPORTS TALENT INTELLIGENCE</div>
                        </div>
                    </div>

                    <div className="aq-brand-center">
                        <div className="aq-badge">
                            <span className="aq-badge-dot" />
                            ORGANIZER WORKSPACE v2.6
                        </div>
                        <h1 className="aq-main-heading">
                            Build better
                            <span className="aq-text-gradient">teams with better insight.</span>
                        </h1>
                        <p className="aq-hero-desc">
                            Manage scouting pipelines, applicant metrics, and performance evaluation workflows from one synchronized command center.
                        </p>

                        <div className="aq-stats-grid">
                            <div className="aq-stat-box">
                                <strong>99.8%</strong>
                                <span>EVALUATION PRECISION</span>
                            </div>
                            <div className="aq-stat-box">
                                <strong>10x</strong>
                                <span>PIPELINE ACCELERATION</span>
                            </div>
                            <div className="aq-stat-box">
                                <strong>REALTIME</strong>
                                <span>TALENT SYNCHRONIZATION</span>
                            </div>
                        </div>
                    </div>

                    <div className="aq-brand-footer">
                        <div className="aq-pulse-indicator">
                            <span className="aq-pulse-core" />
                            <span className="aq-pulse-ring" />
                        </div>
                        <span>ATHLETIQ INTELLIGENCE ENGINE ACTIVE</span>
                    </div>
                </section>

                {/* Form Experience Column */}
                <section className="aq-form-col">
                    <div className="aq-form-card">
                        <div className="aq-form-header">
                            <span className="aq-access-tag">SECURE ACCESS</span>
                            <h2>Sign in</h2>
                            <p>Enter your credentials to access the workspace.</p>
                        </div>

                        {error && (
                            <div className="aq-error-banner" role="alert">
                                <span className="aq-error-icon">!</span>
                                <div>
                                    <strong>Authentication Failed</strong>
                                    <span>{error}</span>
                                </div>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="aq-inputs-form">
                            <div className="aq-input-wrap">
                                <label htmlFor="aq-email-input">EMAIL ADDRESS</label>
                                <div className="aq-input-box">
                                    <span className="aq-icon-prefix">@</span>
                                    <input
                                        id="aq-email-input"
                                        type="email"
                                        value={email}
                                        onChange={(e) => {
                                            setEmail(e.target.value);
                                            if (error) setError("");
                                        }}
                                        placeholder="organizer@athletiq.ai"
                                        autoComplete="email"
                                        required
                                        disabled={loading}
                                    />
                                </div>
                            </div>

                            <div className="aq-input-wrap">
                                <label htmlFor="aq-pass-input">PASSWORD</label>
                                <div className="aq-input-box">
                                    <span className="aq-icon-prefix">
                                        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2.5">
                                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                        </svg>
                                    </span>
                                    <input
                                        id="aq-pass-input"
                                        type={showPassword ? "text" : "password"}
                                        value={password}
                                        onChange={(e) => {
                                            setPassword(e.target.value);
                                            if (error) setError("");
                                        }}
                                        placeholder="Enter your secure password"
                                        autoComplete="current-password"
                                        required
                                        disabled={loading}
                                    />
                                    <button
                                        type="button"
                                        className="aq-eye-btn"
                                        onClick={() => setShowPassword(!showPassword)}
                                        disabled={loading}
                                        aria-label={showPassword ? "Hide password" : "Show password"}
                                    >
                                        <EyeIcon visible={showPassword} />
                                    </button>
                                </div>
                            </div>

                            <button type="submit" className="aq-primary-btn" disabled={loading}>
                                <span className="aq-btn-glow-shimmer" />
                                {loading ? (
                                    <>
                                        <span className="aq-loader-spin" />
                                        <span>AUTHENTICATING...</span>
                                    </>
                                ) : (
                                    <>
                                        <span>SIGN IN TO WORKSPACE</span>
                                        <ArrowIcon />
                                    </>
                                )}
                            </button>
                        </form>

                        <div className="aq-bottom-actions">
                            <span>Don't have an organizer account?</span>
                            <button
                                type="button"
                                onClick={() => navigate("/register")}
                                disabled={loading}
                            >
                                Create Account
                            </button>
                        </div>

                        <div className="aq-encryption-tag">
                            <span className="aq-enc-dot" />
                            END-TO-END QUANTUM ENCRYPTED
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );

    return mounted ? createPortal(content, document.body) : null;
}
