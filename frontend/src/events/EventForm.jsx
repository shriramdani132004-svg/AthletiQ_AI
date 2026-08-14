import { useEffect, useState } from "react";
import { eventApi } from "../api/eventApi";
import "./EventForm.css";

const initialForm = {
    name: "",
    sport: "",
    description: "",
    location: "",
    startDate: "",
    endDate: "",
    registrationDeadline: "",
    playersRequired: 1,
    ageCategory: "",
    eligibilityCriteria: "",
    eventRules: "",
    bannerUrl: ""
};

export default function EventForm({ eventId = null, onSaved }) {
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(Boolean(eventId));
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (!eventId) return;

        async function load() {
            setLoading(true);
            try {
                const event = await eventApi.get(eventId);
                setForm({
                    name: event.name || "",
                    sport: event.sport || "",
                    description: event.description || "",
                    location: event.location || "",
                    startDate: event.startDate || "",
                    endDate: event.endDate || "",
                    registrationDeadline: event.registrationDeadline ? event.registrationDeadline.slice(0, 16) : "",
                    playersRequired: event.playersRequired || 1,
                    ageCategory: event.ageCategory || "",
                    eligibilityCriteria: event.eligibilityCriteria || "",
                    eventRules: event.eventRules || "",
                    bannerUrl: event.bannerUrl || ""
                });
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }

        load();
    }, [eventId]);

    function updateField(event) {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    }

    async function submit(event) {
        event.preventDefault();
        setError("");
        setMessage("");
        setSaving(true);

        try {
            const payload = {
                ...form,
                playersRequired: Number(form.playersRequired),
                registrationDeadline: form.registrationDeadline ? `${form.registrationDeadline}:00` : null
            };

            const saved = eventId
                ? await eventApi.update(eventId, payload)
                : await eventApi.create(payload);

            setMessage(eventId ? "Event updated successfully." : "Event created successfully.");
            if (!eventId) setForm(initialForm);
            if (onSaved) onSaved(saved);
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return <section className="event-form"><p>Loading event...</p></section>;
    }

    return (
        <section className="event-form">
            <header>
                <h1>{eventId ? "Edit Event" : "Create Event"}</h1>
                <p>{eventId ? "Update your draft event details." : "Create a new sports event."}</p>
            </header>

            {error && <div className="event-form-error">{error}</div>}
            {message && <div className="event-form-message">{message}</div>}

            <form onSubmit={submit}>
                <div className="event-form-grid">
                    <label>Event Name<input name="name" value={form.name} onChange={updateField} required maxLength={150} /></label>
                    <label>Sport<input name="sport" value={form.sport} onChange={updateField} required maxLength={100} /></label>
                    <label>Location<input name="location" value={form.location} onChange={updateField} maxLength={500} /></label>
                    <label>Players Required<input name="playersRequired" type="number" min="1" value={form.playersRequired} onChange={updateField} required /></label>
                    <label>Start Date<input name="startDate" type="date" value={form.startDate} onChange={updateField} required /></label>
                    <label>End Date<input name="endDate" type="date" value={form.endDate} onChange={updateField} required /></label>
                    <label>Registration Deadline<input name="registrationDeadline" type="datetime-local" value={form.registrationDeadline} onChange={updateField} required /></label>
                    <label>Age / Category<input name="ageCategory" value={form.ageCategory} onChange={updateField} maxLength={100} /></label>
                </div>

                <label>Description<textarea name="description" value={form.description} onChange={updateField} rows="4" maxLength={5000} /></label>
                <label>Eligibility Criteria<textarea name="eligibilityCriteria" value={form.eligibilityCriteria} onChange={updateField} rows="4" maxLength={5000} /></label>
                <label>Event Rules<textarea name="eventRules" value={form.eventRules} onChange={updateField} rows="4" maxLength={5000} /></label>
                <label>Event Banner URL<input name="bannerUrl" value={form.bannerUrl} onChange={updateField} maxLength={1000} /></label>

                <button type="submit" disabled={saving}>
                    {saving ? "Saving..." : eventId ? "Save Event" : "Create Event"}
                </button>
            </form>
        </section>
    );
}