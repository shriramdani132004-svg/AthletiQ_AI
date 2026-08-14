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

    async function loadEvent() {
        setLoading(true);
        setError("");
        try {
            const data = await eventApi.get(eventId);
            setEvent(data);
        } catch (err) {
            setError(err.message);
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
            setError(err.message);
        }
    }

    if (loading) {
        return <main className="event-details-page"><p>Loading event...</p></main>;
    }

    if (!event) {
        return <main className="event-details-page"><p>{error || "Event not found."}</p></main>;
    }

    return (
        <main className="event-details-page">
            <header className="event-details-header">
                <div>
                    <button type="button" onClick={() => navigate("/events")}>Back to Events</button>
                    <h1>{event.name}</h1>
                    <p>{event.sport}</p>
                </div>
                <span className="event-details-status">{event.status}</span>
            </header>

            {message && <div className="event-details-message">{message}</div>}
            {error && <div className="event-details-error">{error}</div>}

            <section className="event-details-card">
                <h2>Event Information</h2>
                <p>{event.description || "No description provided."}</p>
                <div className="event-details-grid">
                    <div><strong>Location</strong><span>{event.location || "Not specified"}</span></div>
                    <div><strong>Start Date</strong><span>{event.startDate}</span></div>
                    <div><strong>End Date</strong><span>{event.endDate}</span></div>
                    <div><strong>Registration Deadline</strong><span>{event.registrationDeadline || "Not specified"}</span></div>
                    <div><strong>Players Required</strong><span>{event.playersRequired}</span></div>
                    <div><strong>Age / Category</strong><span>{event.ageCategory || "Not specified"}</span></div>
                </div>
            </section>

            <section className="event-details-card">
                <h2>Eligibility</h2>
                <p>{event.eligibilityCriteria || "No eligibility criteria provided."}</p>
            </section>

            <section className="event-details-card">
                <h2>Event Rules</h2>
                <p>{event.eventRules || "No event rules provided."}</p>
            </section>

            {event.bannerUrl && (
                <section className="event-details-card">
                    <h2>Event Banner</h2>
                    <img src={event.bannerUrl} alt={`${event.name} banner`} className="event-banner" />
                </section>
            )}

            <section className="event-details-card">
                <h2>Lifecycle Controls</h2>
                <div className="event-details-actions">
                    {event.status === "DRAFT" && (
                        <>
                            <button type="button" onClick={() => navigate(`/events/${event.id}/edit`)}>Edit</button>
                            <button type="button" onClick={() => perform(eventApi.publish, "Event published.")}>Publish</button>
                        </>
                    )}
                    {event.status === "PUBLISHED" && (
                        <button type="button" onClick={() => perform(eventApi.openApplications, "Applications opened.")}>Open Applications</button>
                    )}
                    {event.status === "APPLICATIONS_OPEN" && (
                        <button type="button" onClick={() => perform(eventApi.pauseApplications, "Applications paused.")}>Pause Applications</button>
                    )}
                    {event.status === "APPLICATIONS_CLOSED" && (
                        <>
                            <button type="button" onClick={() => perform(eventApi.reopenApplications, "Applications reopened.")}>Reopen Applications</button>
                            <button type="button" onClick={() => perform(eventApi.archive, "Event archived.")}>Archive</button>
                        </>
                    )}
                    {event.status === "COMPLETED" && (
                        <button type="button" onClick={() => perform(eventApi.archive, "Event archived.")}>Archive</button>
                    )}
                    {event.status !== "ARCHIVED" && (
                        <button type="button" onClick={() => perform(eventApi.duplicate, "Event duplicated.")}>Duplicate Event</button>
                    )}
                </div>
            </section>
        </main>
    );
}