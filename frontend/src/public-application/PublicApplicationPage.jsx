import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { publicApplicationApi } from "./publicApplicationApi";
import "./PublicApplicationPage.css";

function DynamicPublicField({
    field,
    value,
    error,
    onChange
}) {
    const type =
        String(field.fieldType || "TEXT").toUpperCase();

    const id = `public-field-${field.id}`;

    return (
        <div
            className={
                "public-application-form-field" +
                (error ? " has-error" : "")
            }
        >
            <label htmlFor={id}>
                {field.label}
                {field.required && <span>*</span>}
            </label>

            {type === "LONG_TEXT" ? (
                <textarea
                    id={id}
                    name={field.fieldKey}
                    value={value ?? ""}
                    onChange={e => onChange(e.target.value)}
                    rows={5}
                    placeholder={field.description || ""}
                    required={field.required}
                />
            ) : (
                <input
                    id={id}
                    name={field.fieldKey}
                    value={value ?? ""}
                    onChange={e => onChange(e.target.value)}
                    type={
                        type === "EMAIL"
                            ? "email"
                            : type === "PHONE"
                                ? "tel"
                                : type === "NUMBER"
                                    ? "number"
                                    : type === "DATE"
                                        ? "date"
                                        : type === "URL"
                                            ? "url"
                                            : "text"
                    }
                    inputMode={
                        type === "PHONE"
                            ? "tel"
                            : type === "NUMBER"
                                ? "decimal"
                                : undefined
                    }
                    placeholder={field.description || ""}
                    required={field.required}
                />
            )}

            {field.description && (
                <small className="public-application-field-help">
                    {field.description}
                </small>
            )}

            {error && (
                <small className="public-application-field-error">
                    {error}
                </small>
            )}
        </div>
    );
}

export default function PublicApplicationPage() {

    const { publicCode } = useParams();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [answers, setAnswers] = useState({});
    const [fieldErrors, setFieldErrors] = useState({});

    const [submitting, setSubmitting] = useState(false);
    const [submission, setSubmission] = useState(null);

    useEffect(() => {

        async function load() {

            setLoading(true);
            setError("");

            try {

                const result =
                    await publicApplicationApi.get(
                        publicCode
                    );

                setData(result);

            } catch (err) {

                setError(
                    err.message ||
                    "This application link is unavailable."
                );

            } finally {

                setLoading(false);
            }
        }

        load();

    }, [publicCode]);

    function parseValidationConfig(field) {

        if (!field.validationConfig) {
            return {};
        }

        try {
            return JSON.parse(
                field.validationConfig
            );
        } catch {
            return {};
        }
    }

    function validateField(field, rawValue) {

        const value =
            typeof rawValue === "string"
                ? rawValue.trim()
                : rawValue;

        if (
            field.required &&
            (
                value === "" ||
                value === null ||
                value === undefined
            )
        ) {
            return "This field is required.";
        }

        if (
            value === "" ||
            value === null ||
            value === undefined
        ) {
            return "";
        }

        const type =
            String(field.fieldType || "TEXT")
                .toUpperCase();

        const config =
            parseValidationConfig(field);

        if(type === "EMAIL") {

            const emailPattern =
                /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if(!emailPattern.test(value)) {
                return "Enter a valid email address.";
            }
        }

        if(type === "URL") {

            try {
                new URL(value);
            } catch {
                return "Enter a valid URL.";
            }
        }

        if(type === "NUMBER") {

            const number=Number(value);

            if(!Number.isFinite(number)) {
                return "Enter a valid number.";
            }

            if(
                config.min !== undefined &&
                number < Number(config.min)
            ) {
                return `Minimum value is ${config.min}.`;
            }

            if(
                config.max !== undefined &&
                number > Number(config.max)
            ) {
                return `Maximum value is ${config.max}.`;
            }
        }

        if(
            config.minLength !== undefined &&
            value.length < Number(config.minLength)
        ) {
            return `Minimum ${config.minLength} characters required.`;
        }

        if(
            config.maxLength !== undefined &&
            value.length > Number(config.maxLength)
        ) {
            return `Maximum ${config.maxLength} characters allowed.`;
        }

        return "";
    }

    function updateAnswer(field, value) {

        setAnswers(current => ({
            ...current,
            [field.fieldKey]: value
        }));

        const message =
            validateField(field, value);

        setFieldErrors(current => ({
            ...current,
            [field.fieldKey]: message
        }));

        setError("");
    }

    function validateApplication() {

        const errors = {};

        for(const field of data.fields || []) {

            const message =
                validateField(
                    field,
                    answers[field.fieldKey]
                );

            if(message) {
                errors[field.fieldKey] = message;
            }
        }

        setFieldErrors(errors);

        return Object.keys(errors).length === 0;
    }

    async function handleSubmit() {

        const valid = validateApplication();

        if(!valid) {

            setError(
                "Please correct the highlighted fields before continuing."
            );

            return;
        }

        setError("");
        setSubmitting(true);

        try {

            const result =
                await publicApplicationApi.submit(
                    publicCode,
                    answers
                );

            setSubmission(result);

        } catch (err) {

            setError(
                err.message ||
                "Application submission failed."
            );

        } finally {

            setSubmitting(false);
        }
    }

    if (submission) {

        return (
            <main className="public-application-page">

                <section className="public-application-success">

                    <div className="public-application-success-icon">
                        ✓
                    </div>

                    <span className="public-application-eyebrow">
                        APPLICATION SUBMITTED
                    </span>

                    <h1>
                        Application submitted successfully
                    </h1>

                    <p>
                        Your application has been received by the
                        event organizer.
                    </p>

                    <div className="public-application-reference">

                        <span>
                            Application ID
                        </span>

                        <strong>
                            {submission.applicationId}
                        </strong>

                    </div>

                </section>

            </main>
        );
    }

    if (loading) {

        return (
            <main className="public-application-page">

                <div className="public-application-loading">

                    <div className="public-application-spinner" />

                    <p>
                        Loading application...
                    </p>

                </div>

            </main>
        );
    }

    if (error && !data) {

        return (
            <main className="public-application-page">

                <section className="public-application-error">

                    <div className="public-application-brand">
                        AthletiQ
                    </div>

                    <div className="public-application-error-icon">
                        !
                    </div>

                    <h1>
                        Application unavailable
                    </h1>

                    <p>
                        {error ||
                            "This application link is no longer available."}
                    </p>

                </section>

            </main>
        );
    }

    return (
        <main className="public-application-page">

            <header className="public-application-header">

                <div className="public-application-brand">
                    AthletiQ
                </div>

                <span className="public-application-code">
                    {publicCode}
                </span>

            </header>

            <section className="public-application-hero">

                <div className="public-application-hero-content">

                    <span className="public-application-eyebrow">
                        PLAYER APPLICATION
                    </span>

                    <h1>
                        {data.eventName}
                    </h1>

                    <p className="public-application-sport">
                        {data.sport}
                    </p>

                    {data.description && (
                        <p className="public-application-description">
                            {data.description}
                        </p>
                    )}

                </div>

            </section>

            <section className="public-application-content">

                <div className="public-application-info-grid">

                    <article>
                        <span>Location</span>
                        <strong>
                            {data.location || "To be announced"}
                        </strong>
                    </article>

                    <article>
                        <span>Event Dates</span>
                        <strong>
                            {data.startDate}
                            {" – "}
                            {data.endDate}
                        </strong>
                    </article>

                    <article>
                        <span>Registration Deadline</span>
                        <strong>
                            {data.registrationDeadline
                                ? new Date(
                                    data.registrationDeadline
                                ).toLocaleString()
                                : "Not specified"}
                        </strong>
                    </article>

                    <article>
                        <span>Players Required</span>
                        <strong>
                            {data.playersRequired ??
                                "Not specified"}
                        </strong>
                    </article>

                </div>

                <div className="public-application-columns">

                    <div>

                        <section className="public-application-card">

                            <div className="public-application-section-heading">

                                <span>
                                    01
                                </span>

                                <div>

                                    <h2>
                                        Eligibility
                                    </h2>

                                    <p>
                                        Review the requirements
                                        before applying.
                                    </p>

                                </div>

                            </div>

                            <div className="public-application-copy">

                                {data.eligibilityCriteria ||
                                    "No additional eligibility information provided."}

                            </div>

                        </section>

                        <section className="public-application-card">

                            <div className="public-application-section-heading">

                                <span>
                                    02
                                </span>

                                <div>

                                    <h2>
                                        Instructions
                                    </h2>

                                    <p>
                                        Important information
                                        for applicants.
                                    </p>

                                </div>

                            </div>

                            <div className="public-application-copy">

                                {data.eventRules ||
                                    "Complete all required application fields accurately."}

                            </div>

                        </section>

                    </div>

                    <section className="public-application-card public-application-form-preview">

                        <div className="public-application-section-heading">

                            <span>
                                03
                            </span>

                            <div>

                                <h2>
                                    Application Form
                                </h2>

                                <p>
                                    Published form version{" "}
                                    {data.formVersionNumber}
                                </p>

                            </div>

                        </div>

                        <div className="public-application-form-fields">

                            {data.fields?.map(field => (

                                <DynamicPublicField
                                    key={field.id}
                                    field={field}
                                    value={
                                        answers[field.fieldKey]
                                    }
                                    error={
                                        fieldErrors[
                                            field.fieldKey
                                        ]
                                    }
                                    onChange={value =>
                                        updateAnswer(
                                            field,
                                            value
                                        )
                                    }
                                />

                            ))}

                        </div>

                        <button
                            type="button"
                            className="public-application-primary"
                            disabled={submitting}
                            onClick={handleSubmit}
                        >
                            {submitting
                                ? "Submitting..."
                                : "Submit Application"}
                        </button>

                        {error && (
                            <div className="public-application-submit-error">
                                {error}
                            </div>
                        )}

                    </section>

                </div>

            </section>

            <footer className="public-application-footer">
                Powered by AthletiQ
            </footer>

        </main>
    );
}