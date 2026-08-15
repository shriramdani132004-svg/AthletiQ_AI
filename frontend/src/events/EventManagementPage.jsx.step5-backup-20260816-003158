import { useEffect, useState } from "react";
import { eventApi } from "../api/eventApi";

export default function EventManagementPage() {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function loadEvents() {
        setLoading(true);
        setError("");

        try {
            const data = await eventApi.list();
            setEvents(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadEvents();
    }, []);

    async function perform(action, eventId, successMessage) {
        setError("");
        setMessage("");

        try {
            await action(eventId);
            setMessage(successMessage);
            await loadEvents();
        } catch (err) {
            setError(err.message);
        }
    }

    if (loading) {
        return <main className="events-page"><h1>Events</h1><p>Loading events...</p></main>;
    }

    return (
        <main className="events-page">
            <header className="events-header">
                <div>
                    <div><h1>Event Management</h1>
                    <p>Create, publish and control your sports events.</p></div><a href="/events/create">Create Event</a>
                </div>
            </header>

            {message && <p className="event-message">{message}</p>}
            {error && <p className="event-error">{error}</p>}

            {events.length === 0 ? (
                <section className="empty-events">
                    <h2>No events yet</h2>
                    <p>Create your first sports event to begin the selection workflow.</p>
                </section>
            ) : (
                <section className="event-grid">
                    {events.map((event) => (
                        <article className="event-card" key={event.id}>
                            <div className="event-card-header">
                                <div>
                                    <h2><a href={`/events/${event.id}`}>{event.name}</a></h2>
                                    <p>{event.sport}</p>
                                </div>
                                <span className="event-status">{event.status}</span>
                            </div>

                            <p>{event.description || "No description provided."}</p>
                            <dl>
                                <div><dt>Location</dt><dd>{event.location || "Not specified"}</dd></div>
                                <div><dt>Start</dt><dd>{event.startDate}</dd></div>
                                <div><dt>End</dt><dd>{event.endDate}</dd></div>
                                <div><dt>Players</dt><dd>{event.playersRequired}</dd></div>
                            </dl>

                            <div className="event-actions">
                                {event.status === "DRAFT" && (
                                    <button onClick={() => perform(eventApi.publish, event.id, "Event published.")}>Publish</button>
                                )}
                                {event.status === "PUBLISHED" && (
                                    <button onClick={() => perform(eventApi.openApplications, event.id, "Applications opened.")}>Open Applications</button>
                                )}
                                {event.status === "APPLICATIONS_OPEN" && (
                                    <button onClick={() => perform(eventApi.pauseApplications, event.id, "Applications paused.")}>Pause Applications</button>
                                )}
                                {event.status === "APPLICATIONS_CLOSED" && (
                                    <button onClick={() => perform(eventApi.reopenApplications, event.id, "Applications reopened.")}>Reopen</button>
                                )}
                                {event.status !== "ARCHIVED" && (
                                    <button onClick={() => perform(eventApi.archive, event.id, "Event archived.")}>Archive</button>
                                )}
                                <button onClick={() => perform(eventApi.duplicate, event.id, "Event duplicated.")}>Duplicate</button>
                            </div>
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}