import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { eventApi } from "../api/eventApi";
import { applicationApi } from "../api/applicationApi";
import "./Dashboard.css";

function formatDate(value) {

    if(!value){
        return "—";
    }

    const date =
        new Date(value);

    if(Number.isNaN(date.getTime())){
        return value;
    }

    return date.toLocaleDateString();
}

function formatDateTime(value) {

    if(!value){
        return "—";
    }

    const date =
        new Date(value);

    if(Number.isNaN(date.getTime())){
        return value;
    }

    return date.toLocaleString();
}

function statusLabel(status) {

    if(!status){
        return "UNKNOWN";
    }

    return String(status)
        .replaceAll("_", " ");
}

function statusClass(status) {

    return String(status || "unknown")
        .toLowerCase()
        .replaceAll("_", "-");
}

export default function Dashboard() {

    const { user, logout } =
        useAuth();

    const navigate =
        useNavigate();

    const [events, setEvents] =
        useState([]);

    const [eventStats, setEventStats] =
        useState({});

    const [recentApplications, setRecentApplications] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const loadDashboard =
        useCallback(
            async () => {

                setLoading(true);
                setError("");

                try{

                    const eventResult =
                        await eventApi.list();

                    const eventList =
                        Array.isArray(eventResult)
                            ? eventResult
                            : eventResult?.content || [];

                    setEvents(eventList);

                    if(eventList.length === 0){

                        setEventStats({});
                        setRecentApplications([]);
                        return;
                    }

                    const statsEntries =
                        await Promise.all(
                            eventList.map(
                                async event => {

                                    try{

                                        const stats =
                                            await applicationApi.statistics(
                                                event.id
                                            );

                                        return [
                                            event.id,
                                            stats
                                        ];

                                    }catch{

                                        return [
                                            event.id,
                                            {
                                                totalApplications: 0,
                                                pendingEvaluation: 0,
                                                evaluated: 0,
                                                selected: 0,
                                                accepted: 0,
                                                declined: 0
                                            }
                                        ];
                                    }
                                }
                            )
                        );

                    const statsMap =
                        Object.fromEntries(
                            statsEntries
                        );

                    setEventStats(
                        statsMap
                    );

                    const applicationEntries =
                        await Promise.all(
                            eventList.map(
                                async event => {

                                    try{

                                        const result =
                                            await applicationApi.list(
                                                event.id,
                                                {
                                                    page: 0,
                                                    size: 5,
                                                    sort: "submittedAt",
                                                    direction: "desc"
                                                }
                                            );

                                        return (
                                            result?.content || []
                                        ).map(
                                            application => ({
                                                ...application,
                                                eventName:
                                                    event.name,
                                                eventSport:
                                                    event.sport
                                            })
                                        );

                                    }catch{

                                        return [];
                                    }
                                }
                            )
                        );

                    const combined =
                        applicationEntries
                            .flat()
                            .sort(
                                (a,b) =>
                                    new Date(
                                        b.applicationDate
                                    ) -
                                    new Date(
                                        a.applicationDate
                                    )
                            )
                            .slice(0,6);

                    setRecentApplications(
                        combined
                    );

                }catch(err){

                    setError(
                        err.message ||
                        "Unable to load dashboard."
                    );

                }finally{

                    setLoading(false);
                }
            },
            []
        );

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    const totals =
        useMemo(
            () => {

                return events.reduce(
                    (result,event) => {

                        const stats =
                            eventStats[event.id] ||
                            {};

                        result.totalApplications +=
                            Number(
                                stats.totalApplications || 0
                            );

                        result.pendingEvaluation +=
                            Number(
                                stats.pendingEvaluation || 0
                            );

                        result.evaluated +=
                            Number(
                                stats.evaluated || 0
                            );

                        result.selected +=
                            Number(
                                stats.selected || 0
                            );

                        result.accepted +=
                            Number(
                                stats.accepted || 0
                            );

                        result.declined +=
                            Number(
                                stats.declined || 0
                            );

                        if(
                            event.status ===
                            "APPLICATIONS_OPEN"
                        ){

                            result.activeEvents++;
                        }

                        return result;

                    },
                    {
                        activeEvents: 0,
                        totalApplications: 0,
                        pendingEvaluation: 0,
                        evaluated: 0,
                        selected: 0,
                        accepted: 0,
                        declined: 0
                    }
                );

            },
            [
                events,
                eventStats
            ]
        );

    const firstName =
        user?.firstName ||
        user?.email?.split("@")[0] ||
        "Organizer";

    return (
        <main className="dashboard-page">

            <header className="dashboard-topbar">

                <div className="dashboard-brand-block">

                    <div className="dashboard-logo">
                        A
                    </div>

                    <div>
                        <strong>
                            AthletiQ
                        </strong>

                        <span>
                            Sports Talent Intelligence
                        </span>
                    </div>

                </div>

                <div className="dashboard-top-actions">

                    <button
                        type="button"
                        className="dashboard-profile-button"
                        onClick={() =>
                            navigate("/profile")
                        }
                    >
                        {user?.firstName?.charAt(0) || "O"}
                    </button>

                    <button
                        type="button"
                        className="dashboard-logout"
                        onClick={logout}
                    >
                        Logout
                    </button>

                </div>

            </header>

            <section className="dashboard-hero">

                <div>

                    <span className="dashboard-eyebrow">
                        ORGANIZER CONSOLE
                    </span>

                    <h1>
                        Welcome back, {firstName}.
                    </h1>

                    <p>
                        Create events, collect player
                        applications, and move the best
                        talent through your recruitment
                        pipeline.
                    </p>

                </div>

                <div className="dashboard-hero-actions">

                    <button
                        type="button"
                        className="dashboard-primary-button"
                        onClick={() =>
                            navigate("/events/create")
                        }
                    >
                        + Create Event
                    </button>

                    <button
                        type="button"
                        className="dashboard-secondary-button"
                        onClick={() =>
                            navigate("/events")
                        }
                    >
                        Manage Events
                    </button>

                </div>

            </section>

            {error && (

                <div className="dashboard-error">
                    {error}
                </div>

            )}

            <section className="dashboard-metrics">

                <article className="dashboard-metric-card dashboard-metric-primary">

                    <span>
                        Active Events
                    </span>

                    <strong>
                        {loading
                            ? "—"
                            : totals.activeEvents}
                    </strong>

                    <small>
                        Currently accepting applications
                    </small>

                </article>

                <article className="dashboard-metric-card">

                    <span>
                        Total Applications
                    </span>

                    <strong>
                        {loading
                            ? "—"
                            : totals.totalApplications}
                    </strong>

                    <small>
                        Across your events
                    </small>

                </article>

                <article className="dashboard-metric-card">

                    <span>
                        Pending Evaluation
                    </span>

                    <strong>
                        {loading
                            ? "—"
                            : totals.pendingEvaluation}
                    </strong>

                    <small>
                        Candidates awaiting review
                    </small>

                </article>

                <article className="dashboard-metric-card">

                    <span>
                        Selected
                    </span>

                    <strong>
                        {loading
                            ? "—"
                            : totals.selected}
                    </strong>

                    <small>
                        Ready for next stage
                    </small>

                </article>

            </section>

            <section className="dashboard-main-grid">

                <div className="dashboard-primary-column">

                    <div className="dashboard-section-heading">

                        <div>

                            <span>
                                EVENTS
                            </span>

                            <h2>
                                Your recruitment events
                            </h2>

                        </div>

                        <button
                            type="button"
                            onClick={() =>
                                navigate("/events")
                            }
                        >
                            View all →
                        </button>

                    </div>

                    {loading ? (

                        <section className="dashboard-empty-card">
                            Loading your events...
                        </section>

                    ) : events.length === 0 ? (

                        <section className="dashboard-empty-card">

                            <div className="dashboard-empty-icon">
                                +
                            </div>

                            <h3>
                                Create your first event
                            </h3>

                            <p>
                                Start a recruitment campaign,
                                publish your application form,
                                and collect player submissions.
                            </p>

                            <button
                                type="button"
                                className="dashboard-primary-button"
                                onClick={() =>
                                    navigate(
                                        "/events/create"
                                    )
                                }
                            >
                                Create Event
                            </button>

                        </section>

                    ) : (

                        <div className="dashboard-event-grid">

                            {events
                                .slice(0,6)
                                .map(
                                    event => {

                                        const stats =
                                            eventStats[
                                                event.id
                                            ] || {};

                                        return (
                                            <article
                                                key={
                                                    event.id
                                                }
                                                className="dashboard-event-card"
                                            >

                                                <div className="dashboard-event-card-top">

                                                    <div className="dashboard-sport-icon">
                                                        {(
                                                            event.sport ||
                                                            "S"
                                                        ).charAt(0)}
                                                    </div>

                                                    <span
                                                        className={
                                                            `dashboard-status-badge dashboard-status-${statusClass(
                                                                event.status
                                                            )}`
                                                        }
                                                    >
                                                        {statusLabel(
                                                            event.status
                                                        )}
                                                    </span>

                                                </div>

                                                <div className="dashboard-event-card-body">

                                                    <span className="dashboard-event-sport">
                                                        {event.sport ||
                                                            "Sports Event"}
                                                    </span>

                                                    <h3>
                                                        {event.name}
                                                    </h3>

                                                    <p>
                                                        {event.location ||
                                                            "Location not specified"}
                                                    </p>

                                                </div>

                                                <div className="dashboard-event-card-stats">

                                                    <div>
                                                        <strong>
                                                            {
                                                                stats.totalApplications ||
                                                                0
                                                            }
                                                        </strong>

                                                        <span>
                                                            Applications
                                                        </span>
                                                    </div>

                                                    <div>
                                                        <strong>
                                                            {event.playersRequired ??
                                                                "—"}
                                                        </strong>

                                                        <span>
                                                            Players
                                                        </span>
                                                    </div>

                                                </div>

                                                <div className="dashboard-event-card-footer">

                                                    <span>
                                                        {formatDate(
                                                            event.startDate
                                                        )}
                                                    </span>

                                                    <div>

                                                        <button
                                                            type="button"
                                                            onClick={() =>
                                                                navigate(
                                                                    `/events/${event.id}`
                                                                )
                                                            }
                                                        >
                                                            View Event
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

                                                </div>

                                            </article>
                                        );
                                    }
                                )}

                        </div>

                    )}

                </div>

                <aside className="dashboard-side-column">

                    <div className="dashboard-section-heading">

                        <div>

                            <span>
                                ACTIVITY
                            </span>

                            <h2>
                                Recent applications
                            </h2>

                        </div>

                        <button
                            type="button"
                            onClick={() =>
                                navigate("/events")
                            }
                        >
                            All events
                        </button>

                    </div>

                    <section className="dashboard-activity-card">

                        {loading ? (

                            <div className="dashboard-activity-empty">
                                Loading activity...
                            </div>

                        ) : recentApplications.length === 0 ? (

                            <div className="dashboard-activity-empty">

                                <strong>
                                    No applications yet
                                </strong>

                                <span>
                                    New player submissions
                                    will appear here.
                                </span>

                            </div>

                        ) : (

                            recentApplications.map(
                                application => (

                                    <button
                                        type="button"
                                        key={
                                            `${application.eventId}-${application.applicationId}`
                                        }
                                        className="dashboard-activity-item"
                                        onClick={() =>
                                            navigate(
                                                `/events/${application.eventId}/applications`
                                            )
                                        }
                                    >

                                        <div className="dashboard-activity-number">
                                            #{application.applicationId}
                                        </div>

                                        <div className="dashboard-activity-content">

                                            <strong>
                                                {application.playerName ||
                                                    "Public Applicant"}
                                            </strong>

                                            <span>
                                                {application.eventName}
                                            </span>

                                        </div>

                                        <div className="dashboard-activity-meta">

                                            <span
                                                className={
                                                    `dashboard-status-badge dashboard-status-${statusClass(
                                                        application.status
                                                    )}`
                                                }
                                            >
                                                {statusLabel(
                                                    application.status
                                                )}
                                            </span>

                                            <small>
                                                {formatDateTime(
                                                    application.applicationDate
                                                )}
                                            </small>

                                        </div>

                                    </button>
                                )
                            )

                        )}

                    </section>

                </aside>

            </section>

            <section className="dashboard-quick-actions">

                <div className="dashboard-section-heading">

                    <div>

                        <span>
                            QUICK ACTIONS
                        </span>

                        <h2>
                            Keep your workflow moving
                        </h2>

                    </div>

                </div>

                <div className="dashboard-action-grid">

                    <button
                        type="button"
                        onClick={() =>
                            navigate("/events/create")
                        }
                    >
                        <span className="dashboard-action-icon">
                            +
                        </span>

                        <strong>
                            Create Event
                        </strong>

                        <small>
                            Start a new recruitment campaign
                        </small>
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            navigate("/events")
                        }
                    >
                        <span className="dashboard-action-icon">
                            ◫
                        </span>

                        <strong>
                            Manage Events
                        </strong>

                        <small>
                            Review status and event setup
                        </small>
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            events[0] &&
                            navigate(
                                `/events/${events[0].id}/applications`
                            )
                        }
                    >
                        <span className="dashboard-action-icon">
                            ◎
                        </span>

                        <strong>
                            Applications
                        </strong>

                        <small>
                            Review your latest candidates
                        </small>
                    </button>

                    <button
                        type="button"
                        onClick={() =>
                            navigate("/profile")
                        }
                    >
                        <span className="dashboard-action-icon">
                            ◌
                        </span>

                        <strong>
                            Organizer Profile
                        </strong>

                        <small>
                            Manage your account details
                        </small>
                    </button>

                </div>

            </section>

            <footer className="dashboard-footer">

                <div>
                    <strong>
                        AthletiQ
                    </strong>

                    <span>
                        AI-assisted sports recruitment platform
                    </span>
                </div>

                <span>
                    {new Date().getFullYear()}
                </span>

            </footer>

        </main>
    );
}