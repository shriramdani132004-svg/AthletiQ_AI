import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { applicationApi } from "../api/applicationApi";
import "./EventApplicationsPage.css";

export default function EventApplicationsPage() {

    const { eventId } = useParams();
    const navigate = useNavigate();

    const [applications, setApplications] =
        useState([]);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [selected, setSelected] =
        useState(null);

    useEffect(() => {

        async function load() {

            setLoading(true);
            setError("");

            try {

                const result =
                    await applicationApi.list(
                        eventId
                    );

                setApplications(result || []);

            } catch(err) {

                setError(
                    err.message ||
                    "Unable to load applications."
                );

            } finally {

                setLoading(false);
            }
        }

        load();

    }, [eventId]);

    const sortedApplications =
        useMemo(
            () =>
                [...applications].sort(
                    (a, b) =>
                        new Date(
                            b.submittedAt
                        ) -
                        new Date(
                            a.submittedAt
                        )
                ),
            [applications]
        );

    function parseAnswers(application) {

        if(!application.submittedData){
            return {};
        }

        try {
            return JSON.parse(
                application.submittedData
            );
        } catch {
            return {};
        }
    }

    if(loading){

        return (
            <main className="applications-page">
                <div className="applications-state">
                    Loading applications...
                </div>
            </main>
        );
    }

    if(error){

        return (
            <main className="applications-page">
                <div className="applications-state applications-state-error">
                    {error}
                </div>
            </main>
        );
    }

    return (
        <main className="applications-page">

            <header className="applications-header">

                <div>

                    <button
                        type="button"
                        className="applications-back"
                        onClick={() =>
                            navigate(
                                `/events/${eventId}`
                            )
                        }
                    >
                        ← Back to Event
                    </button>

                    <span className="applications-eyebrow">
                        EVENT APPLICATIONS
                    </span>

                    <h1>
                        Applications
                    </h1>

                    <p>
                        Review player submissions
                        received for this event.
                    </p>

                </div>

                <div className="applications-count">
                    <strong>
                        {applications.length}
                    </strong>
                    <span>
                        submissions
                    </span>
                </div>

            </header>

            {applications.length === 0 ? (

                <section className="applications-empty">

                    <h2>
                        No applications yet
                    </h2>

                    <p>
                        Player submissions will
                        appear here once they
                        apply through the public
                        application link.
                    </p>

                </section>

            ) : (

                <section className="applications-list">

                    {sortedApplications.map(
                        application => {

                            const answers =
                                parseAnswers(
                                    application
                                );

                            return (
                                <article
                                    key={
                                        application.id
                                    }
                                    className="application-row"
                                    onClick={() =>
                                        setSelected(
                                            application
                                        )
                                    }
                                >

                                    <div className="application-number">
                                        #{application.id}
                                    </div>

                                    <div className="application-summary">

                                        <strong>
                                            {answers.phone ||
                                                "Public Applicant"}
                                        </strong>

                                        <span>
                                            Form version{" "}
                                            {
                                                application
                                                    .formVersionNumber
                                            }
                                        </span>

                                    </div>

                                    <div className="application-date">
                                        {
                                            new Date(
                                                application.submittedAt
                                            ).toLocaleString()
                                        }
                                    </div>

                                    <div className="application-arrow">
                                        →
                                    </div>

                                </article>
                            );
                        }
                    )}

                </section>
            )}

            {selected && (

                <div
                    className="application-modal-backdrop"
                    onClick={() =>
                        setSelected(null)
                    }
                >

                    <section
                        className="application-modal"
                        onClick={event =>
                            event.stopPropagation()
                        }
                    >

                        <div className="application-modal-header">

                            <div>

                                <span>
                                    APPLICATION #
                                    {selected.id}
                                </span>

                                <h2>
                                    Player Submission
                                </h2>

                            </div>

                            <button
                                type="button"
                                onClick={() =>
                                    setSelected(null)
                                }
                            >
                                ×
                            </button>

                        </div>

                        <div className="application-modal-body">

                            <div className="application-detail">

                                <span>
                                    Form Version
                                </span>

                                <strong>
                                    {selected.formVersionNumber}
                                </strong>

                            </div>

                            <div className="application-detail">

                                <span>
                                    Submitted
                                </span>

                                <strong>
                                    {new Date(
                                        selected.submittedAt
                                    ).toLocaleString()}
                                </strong>

                            </div>

                            <div className="application-answers">

                                <h3>
                                    Submitted Answers
                                </h3>

                                {Object.entries(
                                    parseAnswers(
                                        selected
                                    )
                                ).map(
                                    ([key, value]) => (

                                        <div
                                            key={key}
                                            className="application-answer"
                                        >

                                            <span>
                                                {key}
                                            </span>

                                            <strong>
                                                {String(
                                                    value
                                                )}
                                            </strong>

                                        </div>

                                    )
                                )}

                            </div>

                        </div>

                    </section>

                </div>
            )}

        </main>
    );
}