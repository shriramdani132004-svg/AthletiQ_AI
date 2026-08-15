import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { eventApi } from "../api/eventApi";
import "./EventDetailsPage.css";

export default function EventDetailsPage() {
    const { eventId } = useParams();
    const navigate = useNavigate();

    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [publicLink, setPublicLink] = useState(null);
    const [publicLinkLoading, setPublicLinkLoading] = useState(false);

    async function loadEvent() {
        setLoading(true);
        setError("");

        try {
            const data = await eventApi.get(eventId);
            setEvent(data);
        } catch (err) {
            setError(
                err.message ||
                "Unable to load event."
            );
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadEvent();
    }, [eventId]);

    async function perform(action, successMessage) {
        setError("");
        setMessage("");

        try {
            const updated = await action(eventId);
            setEvent(updated);
            setMessage(successMessage);
        } catch (err) {
            setError(
                err.message ||
                "Event action failed."
            );
        }
    }

    async function generatePublicLink() {

        setError("");
        setMessage("");
        setPublicLinkLoading(true);

        try {

            const result =
                await eventApi.publicApplication(
                    eventId
                );

            setPublicLink(result);

            setMessage(
                "Public application link ready."
            );

        } catch (err) {

            setError(
                err.message ||
                "Unable to generate public application link."
            );

        } finally {

            setPublicLinkLoading(false);
        }
    }

    async function copyPublicLink() {

        if(!publicLink?.publicUrl){
            return;
        }

        try {

            await navigator.clipboard.writeText(
                publicLink.publicUrl
            );

            setMessage(
                "Public application link copied."
            );

        } catch {

            setError(
                "Unable to copy the public application link."
            );
        }
    }

    function openPublicLink() {

        if(!publicLink?.publicUrl){
            return;
        }

        window.open(
            publicLink.publicUrl,
            "_blank",
            "noopener,noreferrer"
        );
    }

    function shareWhatsApp() {

        if(!publicLink?.publicUrl){
            return;
        }

        const text =
            `Apply for ${event.name}: ${publicLink.publicUrl}`;

        const url =
            `https://wa.me/?text=${encodeURIComponent(text)}`;

        window.open(
            url,
            "_blank",
            "noopener,noreferrer"
        );
    }

    function shareTelegram() {

        if(!publicLink?.publicUrl){
            return;
        }

        const text =
            `Apply for ${event.name}`;

        const url =
            `https://t.me/share/url?url=${encodeURIComponent(
                publicLink.publicUrl
            )}&text=${encodeURIComponent(text)}`;

        window.open(
            url,
            "_blank",
            "noopener,noreferrer"
        );
    }

    function openQrCode() {

        if(!publicLink?.publicUrl){
            return;
        }

        const qrUrl =
            `https://api.qrserver.com/v1/create-qr-code/?size=320x320&data=${encodeURIComponent(
                publicLink.publicUrl
            )}`;

        window.open(
            qrUrl,
            "_blank",
            "noopener,noreferrer"
        );
    }

    if (loading) {
        return (
            <main className="event-details-page">
                <p>Loading event...</p>
            </main>
        );
    }

    if (!event) {
        return (
            <main className="event-details-page">
                <p>
                    {error || "Event not found."}
                </p>
            </main>
        );
    }

    return (
        <main className="event-details-page">

            <header className="event-details-header">

                <div>

                    <button
                        type="button"
                        onClick={() =>
                            navigate("/events")
                        }
                    >
                        Back to Events
                    </button>

                    <h1>
                        {event.name}
                    </h1>

                    <p>
                        {event.sport}
                    </p>

                </div>

                <span className="event-details-status">
                    {event.status}
                </span>

            </header>

            {message && (
                <div className="event-details-message">
                    {message}
                </div>
            )}

            {error && (
                <div className="event-details-error">
                    {error}
                </div>
            )}

            <section className="event-details-card">

                <h2>
                    Event Information
                </h2>

                <p>
                    {event.description ||
                        "No description provided."}
                </p>

                <div className="event-details-grid">

                    <div>
                        <strong>
                            Location
                        </strong>
                        <span>
                            {event.location ||
                                "Not specified"}
                        </span>
                    </div>

                    <div>
                        <strong>
                            Start Date
                        </strong>
                        <span>
                            {event.startDate}
                        </span>
                    </div>

                    <div>
                        <strong>
                            End Date
                        </strong>
                        <span>
                            {event.endDate}
                        </span>
                    </div>

                    <div>
                        <strong>
                            Registration Deadline
                        </strong>
                        <span>
                            {event.registrationDeadline ||
                                "Not specified"}
                        </span>
                    </div>

                    <div>
                        <strong>
                            Players Required
                        </strong>
                        <span>
                            {event.playersRequired}
                        </span>
                    </div>

                    <div>
                        <strong>
                            Age / Category
                        </strong>
                        <span>
                            {event.ageCategory ||
                                "Not specified"}
                        </span>
                    </div>

                </div>

            </section>

            <section className="event-details-card">

                <h2>
                    Eligibility
                </h2>

                <p>
                    {event.eligibilityCriteria ||
                        "No eligibility criteria provided."}
                </p>

            </section>

            <section className="event-details-card">

                <h2>
                    Event Rules
                </h2>

                <p>
                    {event.eventRules ||
                        "No event rules provided."}
                </p>

            </section>

            {event.bannerUrl && (
                <section className="event-details-card">

                    <h2>
                        Event Banner
                    </h2>

                    <img
                        src={event.bannerUrl}
                        alt={`${event.name} banner`}
                        className="event-banner"
                    />

                </section>
            )}

            <section className="event-details-card">

                <h2>
                    Event Setup
                </h2>

                <p>
                    Configure the application form,
                    player requirements, and evaluation
                    rules for this event.
                </p>

                <div className="event-details-actions">

                    <button
                        type="button"
                        onClick={() =>
                            navigate(
                                `/events/${event.id}/form-builder`
                            )
                        }
                    >
                        Form Builder
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            navigate(
                                `/events/${event.id}/evaluation`
                            )
                        }
                    >
                        Requirements & Evaluation
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            navigate(
                                `/events/${event.id}/applications`
                            )
                        }
                    >
                        Applications
                    </button>

                </div>

            </section>

            <section className="event-details-card event-public-share-card">

                <div className="event-public-share-header">

                    <div>

                        <span className="event-public-share-eyebrow">
                            PUBLIC APPLICATION
                        </span>

                        <h2>
                            Share Player Application
                        </h2>

                        <p>
                            Generate a public application
                            link that players can open
                            without organizer login.
                        </p>

                    </div>

                    <div className="event-public-share-code">

                        {publicLink?.publicCode ||
                            "NOT GENERATED"}

                    </div>

                </div>

                {!publicLink ? (

                    <div className="event-public-share-empty">

                        <p>
                            Generate the public application
                            link for this event.
                        </p>

                        <button
                            type="button"
                            onClick={generatePublicLink}
                            disabled={publicLinkLoading}
                        >
                            {publicLinkLoading
                                ? "Generating..."
                                : "Generate Public Link"}
                        </button>

                    </div>

                ) : (

                    <div className="event-public-share-content">

                        <div className="event-public-share-url">

                            <span>
                                Public URL
                            </span>

                            <input
                                type="text"
                                value={
                                    publicLink.publicUrl
                                }
                                readOnly
                            />

                        </div>

                        <div className="event-details-actions">

                            <button
                                type="button"
                                onClick={copyPublicLink}
                            >
                                Copy Link
                            </button>

                            <button
                                type="button"
                                onClick={openPublicLink}
                            >
                                Open Public Page
                            </button>

                            <button
                                type="button"
                                onClick={shareWhatsApp}
                            >
                                WhatsApp
                            </button>

                            <button
                                type="button"
                                onClick={shareTelegram}
                            >
                                Telegram
                            </button>

                            <button
                                type="button"
                                onClick={openQrCode}
                            >
                                Open QR Code
                            </button>

                        </div>

                    </div>

                )}

            </section>

            <section className="event-details-card">

                <h2>
                    Lifecycle Controls
                </h2>

                <div className="event-details-actions">

                    {event.status === "DRAFT" && (
                        <>
                            <button
                                type="button"
                                onClick={() =>
                                    navigate(
                                        `/events/${event.id}/edit`
                                    )
                                }
                            >
                                Edit
                            </button>

                            <button
                                type="button"
                                onClick={() =>
                                    perform(
                                        eventApi.publish,
                                        "Event published."
                                    )
                                }
                            >
                                Publish
                            </button>
                        </>
                    )}

                    {event.status === "PUBLISHED" && (
                        <button
                            type="button"
                            onClick={() =>
                                perform(
                                    eventApi.openApplications,
                                    "Applications opened."
                                )
                            }
                        >
                            Open Applications
                        </button>
                    )}

                    {event.status === "APPLICATIONS_OPEN" && (
                        <button
                            type="button"
                            onClick={() =>
                                perform(
                                    eventApi.pauseApplications,
                                    "Applications paused."
                                )
                            }
                        >
                            Pause Applications
                        </button>
                    )}

                    {event.status === "APPLICATIONS_CLOSED" && (
                        <>
                            <button
                                type="button"
                                onClick={() =>
                                    perform(
                                        eventApi.reopenApplications,
                                        "Applications reopened."
                                    )
                                }
                            >
                                Reopen Applications
                            </button>

                            <button
                                type="button"
                                onClick={() =>
                                    perform(
                                        eventApi.archive,
                                        "Event archived."
                                    )
                                }
                            >
                                Archive
                            </button>
                        </>
                    )}

                    {event.status === "COMPLETED" && (
                        <button
                            type="button"
                            onClick={() =>
                                perform(
                                    eventApi.archive,
                                    "Event archived."
                                )
                            }
                        >
                            Archive
                        </button>
                    )}

                    {event.status !== "ARCHIVED" && (
                        <button
                            type="button"
                            onClick={() =>
                                perform(
                                    eventApi.duplicate,
                                    "Event duplicated."
                                )
                            }
                        >
                            Duplicate Event
                        </button>
                    )}

                </div>

            </section>

        </main>
    );
}