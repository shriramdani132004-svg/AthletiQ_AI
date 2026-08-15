import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { eventApi } from "../api/eventApi";
import { requirementsApi } from "../api/requirementsApi";
import { evaluationCriteriaApi } from "../api/evaluationCriteriaApi";
import "./EventEvaluationPage.css";

const EMPTY_REQUIREMENTS = {
    requiredPositions: "",
    minAge: "",
    maxAge: "",
    minimumExperience: "",
    requiredAchievements: "",
    requiredSkills: "",
    performanceRequirements: "",
    fitnessRequirements: "",
    availabilityRequirements: "",
    eligibilityConditions: "",
    eventSpecificRequirements: ""
};

const EMPTY_CRITERION = {
    name: "",
    description: "",
    weight: "",
    minScore: 0,
    maxScore: 100,
    criterionType: "NUMERIC",
    enabled: true,
    displayOrder: 0
};

function normalizeRequirements(data) {
    return {
        requiredPositions: data?.requiredPositions ?? "",
        minAge: data?.minAge ?? "",
        maxAge: data?.maxAge ?? "",
        minimumExperience: data?.minimumExperience ?? "",
        requiredAchievements: data?.requiredAchievements ?? "",
        requiredSkills: data?.requiredSkills ?? "",
        performanceRequirements: data?.performanceRequirements ?? "",
        fitnessRequirements: data?.fitnessRequirements ?? "",
        availabilityRequirements: data?.availabilityRequirements ?? "",
        eligibilityConditions: data?.eligibilityConditions ?? "",
        eventSpecificRequirements:
            data?.eventSpecificRequirements ?? ""
    };
}

function normalizeCriterion(criterion, fallbackOrder = 0) {
    return {
        name: criterion?.name ?? "",
        description: criterion?.description ?? "",
        weight: criterion?.weight ?? "",
        minScore: criterion?.minScore ?? 0,
        maxScore: criterion?.maxScore ?? 100,
        criterionType: criterion?.criterionType ?? "NUMERIC",
        enabled: criterion?.enabled ?? true,
        displayOrder:
            criterion?.displayOrder ?? fallbackOrder
    };
}

export default function EventEvaluationPage() {
    const { eventId } = useParams();
    const navigate = useNavigate();

    const [event, setEvent] = useState(null);

    const [requirements, setRequirements] =
        useState(EMPTY_REQUIREMENTS);

    const [criteria, setCriteria] = useState([]);
    const [criteriaValidation, setCriteriaValidation] =
        useState(null);

    const [loading, setLoading] = useState(true);
    const [savingRequirements, setSavingRequirements] =
        useState(false);
    const [savingCriterion, setSavingCriterion] =
        useState(false);
    const [deletingCriterionId, setDeletingCriterionId] =
        useState(null);

    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [criterionError, setCriterionError] =
        useState("");

    const [showCriterionModal, setShowCriterionModal] =
        useState(false);

    const [editingCriterionId, setEditingCriterionId] =
        useState(null);

    const [criterionForm, setCriterionForm] =
        useState(EMPTY_CRITERION);

    async function loadPage() {
        setLoading(true);
        setError("");

        try {
            const [
                eventData,
                requirementsData,
                criteriaData,
                validationData
            ] = await Promise.all([
                eventApi.get(eventId),
                requirementsApi.get(eventId),
                evaluationCriteriaApi.list(eventId),
                evaluationCriteriaApi.validation(eventId)
            ]);

            setEvent(eventData);
            setRequirements(
                normalizeRequirements(requirementsData)
            );
            setCriteria(criteriaData || []);
            setCriteriaValidation(validationData);
        } catch (err) {
            setError(
                err.message ||
                "Failed to load evaluation configuration."
            );
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadPage();
    }, [eventId]);

    function updateRequirement(field, value) {
        setRequirements(current => ({
            ...current,
            [field]: value
        }));

        setMessage("");
        setError("");
    }

    function openCreateCriterion() {
        setEditingCriterionId(null);

        setCriterionForm({
            ...EMPTY_CRITERION,
            displayOrder: criteria.length
        });

        setCriterionError("");
        setShowCriterionModal(true);
    }

    function openEditCriterion(criterion) {
        setEditingCriterionId(criterion.id);
        setCriterionForm(
            normalizeCriterion(
                criterion,
                criterion.displayOrder ?? 0
            )
        );
        setCriterionError("");
        setShowCriterionModal(true);
    }

    function closeCriterionModal() {
        if (savingCriterion) {
            return;
        }

        setShowCriterionModal(false);
        setEditingCriterionId(null);
        setCriterionForm(EMPTY_CRITERION);
        setCriterionError("");
    }

    function updateCriterion(field, value) {
        setCriterionForm(current => ({
            ...current,
            [field]: value
        }));

        setCriterionError("");
    }

    function numberOrNull(value) {
        if (
            value === "" ||
            value === null ||
            value === undefined
        ) {
            return null;
        }

        const parsed = Number(value);

        return Number.isFinite(parsed) ? parsed : null;
    }

    async function saveRequirements() {
        setSavingRequirements(true);
        setError("");
        setMessage("");

        const minAge = numberOrNull(requirements.minAge);
        const maxAge = numberOrNull(requirements.maxAge);

        if (
            minAge !== null &&
            maxAge !== null &&
            maxAge < minAge
        ) {
            setError(
                "Maximum age cannot be less than minimum age."
            );
            setSavingRequirements(false);
            return;
        }

        try {
            const payload = {
                requiredPositions:
                    requirements.requiredPositions.trim(),

                minAge,
                maxAge,

                minimumExperience:
                    requirements.minimumExperience.trim(),

                requiredAchievements:
                    requirements.requiredAchievements.trim(),

                requiredSkills:
                    requirements.requiredSkills.trim(),

                performanceRequirements:
                    requirements.performanceRequirements.trim(),

                fitnessRequirements:
                    requirements.fitnessRequirements.trim(),

                availabilityRequirements:
                    requirements.availabilityRequirements.trim(),

                eligibilityConditions:
                    requirements.eligibilityConditions.trim(),

                eventSpecificRequirements:
                    requirements.eventSpecificRequirements.trim()
            };

            const saved =
                await requirementsApi.update(
                    eventId,
                    payload
                );

            setRequirements(
                normalizeRequirements(saved)
            );

            setMessage(
                "Player requirements saved successfully."
            );
        } catch (err) {
            setError(
                err.message ||
                "Failed to save player requirements."
            );
        } finally {
            setSavingRequirements(false);
        }
    }

    function validateCriterionForm() {
        const name = criterionForm.name.trim();
        const weight = Number(criterionForm.weight);
        const minScore = Number(criterionForm.minScore);
        const maxScore = Number(criterionForm.maxScore);

        if (!name) {
            return "Criterion name is required.";
        }

        if (
            !Number.isFinite(weight) ||
            weight <= 0 ||
            weight > 100
        ) {
            return (
                "Weight must be greater than 0 and at most 100."
            );
        }

        if (
            !Number.isFinite(minScore) ||
            !Number.isFinite(maxScore)
        ) {
            return "Minimum and maximum scores are required.";
        }

        if (minScore > maxScore) {
            return (
                "Minimum score cannot be greater than maximum score."
            );
        }

        return null;
    }

    async function saveCriterion() {
        setSavingCriterion(true);
        setCriterionError("");
        setError("");
        setMessage("");

        const validationError = validateCriterionForm();

        if (validationError) {
            setCriterionError(validationError);
            setSavingCriterion(false);
            return;
        }

        const payload = {
            name: criterionForm.name.trim(),
            description:
                criterionForm.description.trim(),

            weight: Number(criterionForm.weight),
            minScore: Number(criterionForm.minScore),
            maxScore: Number(criterionForm.maxScore),

            criterionType:
                criterionForm.criterionType,

            enabled:
                Boolean(criterionForm.enabled),

            displayOrder:
                Number(criterionForm.displayOrder)
        };

        try {
            if (editingCriterionId !== null) {
                await evaluationCriteriaApi.update(
                    eventId,
                    editingCriterionId,
                    payload
                );

                setMessage(
                    "Evaluation criterion updated successfully."
                );
            } else {
                await evaluationCriteriaApi.create(
                    eventId,
                    payload
                );

                setMessage(
                    "Evaluation criterion added successfully."
                );
            }

            const [
                refreshedCriteria,
                refreshedValidation
            ] = await Promise.all([
                evaluationCriteriaApi.list(eventId),
                evaluationCriteriaApi.validation(eventId)
            ]);

            setCriteria(
                refreshedCriteria || []
            );

            setCriteriaValidation(
                refreshedValidation
            );

            closeCriterionModal();
        } catch (err) {
            setCriterionError(
                err.message ||
                "Failed to save evaluation criterion."
            );
        } finally {
            setSavingCriterion(false);
        }
    }

    async function deleteCriterion(criterion) {
        const confirmed = window.confirm(
            `Delete "${criterion.name}"? This cannot be undone.`
        );

        if (!confirmed) {
            return;
        }

        setDeletingCriterionId(criterion.id);
        setError("");
        setMessage("");

        try {
            await evaluationCriteriaApi.delete(
                eventId,
                criterion.id
            );

            const [
                refreshedCriteria,
                refreshedValidation
            ] = await Promise.all([
                evaluationCriteriaApi.list(eventId),
                evaluationCriteriaApi.validation(eventId)
            ]);

            setCriteria(
                refreshedCriteria || []
            );

            setCriteriaValidation(
                refreshedValidation
            );

            setMessage(
                "Evaluation criterion deleted successfully."
            );
        } catch (err) {
            setError(
                err.message ||
                "Failed to delete evaluation criterion."
            );
        } finally {
            setDeletingCriterionId(null);
        }
    }

    const enabledCriteriaCount =
        criteria.filter(criterion => criterion.enabled).length;

    const disabledCriteriaCount =
        criteria.filter(criterion => !criterion.enabled).length;

    const activeWeight =
        Number(criteriaValidation?.activeWeightTotal ?? 0);

    const weightPercentage =
        Math.min(
            Math.max(activeWeight, 0),
            100
        );

    const evaluationReady =
        criteriaValidation?.valid === true;
    if (loading) {
        return (
            <main className="event-evaluation-page">
                <div className="event-evaluation-loading">
                    <div className="event-evaluation-spinner" />
                    <p>
                        Loading evaluation configuration...
                    </p>
                </div>
            </main>
        );
    }

    if (!event) {
        return (
            <main className="event-evaluation-page">
                <section className="event-evaluation-card">
                    <button
                        type="button"
                        className="event-evaluation-back"
                        onClick={() => navigate("/events")}
                    >
                        ← Back to Events
                    </button>

                    <h1>Evaluation Configuration</h1>

                    <p>
                        {error || "Event not found."}
                    </p>
                </section>
            </main>
        );
    }

    return (
        <main className="event-evaluation-page">

            <header className="event-evaluation-header">

                <button
                    type="button"
                    className="event-evaluation-back"
                    onClick={() =>
                        navigate(`/events/${eventId}`)
                    }
                >
                    ← Back to Event
                </button>

                <div className="event-evaluation-title-row">

                    <div>
                        <p className="event-evaluation-eyebrow">
                            EVENT CONFIGURATION
                        </p>

                        <h1>
                            {event.name}
                        </h1>

                        <p className="event-evaluation-subtitle">
                            Define who is suitable for this
                            event and how candidates will be
                            evaluated.
                        </p>
                    </div>

                    <span className="event-evaluation-status">
                        {event.status}
                    </span>
                </div>
            </header>

            {message && (
                <div className="event-evaluation-alert success">
                    <strong>Saved</strong>
                    <span>{message}</span>
                </div>
            )}

            {error && (
                <div className="event-evaluation-alert error">
                    <strong>Unable to complete</strong>
                    <span>{error}</span>
                </div>
            )}

            {/* =================================================
                PLAYER REQUIREMENTS
            ================================================= */}

            <section className="event-evaluation-card">

                <div className="event-evaluation-section-heading">

                    <span className="event-evaluation-section-icon">
                        01
                    </span>

                    <div>
                        <h2>Player Requirements</h2>

                        <p>
                            Describe the type of player you
                            are looking for. These requirements
                            will later be used for eligibility
                            and evaluation.
                        </p>
                    </div>
                </div>

                <div className="event-evaluation-grid">

                    <label className="event-evaluation-field full">
                        <span>Required Positions</span>

                        <input
                            value={
                                requirements.requiredPositions
                            }
                            onChange={event =>
                                updateRequirement(
                                    "requiredPositions",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "e.g. Batsman, All-rounder"
                            }
                        />

                        <small>
                            Separate multiple positions
                            with commas.
                        </small>
                    </label>

                    <label className="event-evaluation-field">
                        <span>Minimum Age</span>

                        <input
                            type="number"
                            min="0"
                            max="120"
                            value={requirements.minAge}
                            onChange={event =>
                                updateRequirement(
                                    "minAge",
                                    event.target.value
                                )
                            }
                            placeholder="18"
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Maximum Age</span>

                        <input
                            type="number"
                            min="0"
                            max="120"
                            value={requirements.maxAge}
                            onChange={event =>
                                updateRequirement(
                                    "maxAge",
                                    event.target.value
                                )
                            }
                            placeholder="27"
                        />
                    </label>

                    <label className="event-evaluation-field full">
                        <span>Minimum Experience</span>

                        <input
                            value={
                                requirements.minimumExperience
                            }
                            onChange={event =>
                                updateRequirement(
                                    "minimumExperience",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "e.g. At least 3 years of competitive experience"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Required Achievements</span>

                        <textarea
                            value={
                                requirements.requiredAchievements
                            }
                            onChange={event =>
                                updateRequirement(
                                    "requiredAchievements",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "e.g. District or state-level achievements"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Required Skills</span>

                        <textarea
                            value={
                                requirements.requiredSkills
                            }
                            onChange={event =>
                                updateRequirement(
                                    "requiredSkills",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "e.g. Batting, fielding, match awareness"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Performance Requirements</span>

                        <textarea
                            value={
                                requirements.performanceRequirements
                            }
                            onChange={event =>
                                updateRequirement(
                                    "performanceRequirements",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "Describe expected recent performance"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Fitness Requirements</span>

                        <textarea
                            value={
                                requirements.fitnessRequirements
                            }
                            onChange={event =>
                                updateRequirement(
                                    "fitnessRequirements",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "Describe fitness expectations"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Availability Requirements</span>

                        <textarea
                            value={
                                requirements.availabilityRequirements
                            }
                            onChange={event =>
                                updateRequirement(
                                    "availabilityRequirements",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "e.g. Available throughout tournament dates"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field">
                        <span>Eligibility Conditions</span>

                        <textarea
                            value={
                                requirements.eligibilityConditions
                            }
                            onChange={event =>
                                updateRequirement(
                                    "eligibilityConditions",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "Describe mandatory eligibility conditions"
                            }
                        />
                    </label>

                    <label className="event-evaluation-field full">
                        <span>
                            Event-specific Requirements
                        </span>

                        <textarea
                            value={
                                requirements
                                    .eventSpecificRequirements
                            }
                            onChange={event =>
                                updateRequirement(
                                    "eventSpecificRequirements",
                                    event.target.value
                                )
                            }
                            placeholder={
                                "Add additional event-specific requirements"
                            }
                        />
                    </label>

                </div>

                <div className="event-evaluation-footer">

                    <span className="event-evaluation-save-note">
                        Requirements are saved to this event.
                    </span>

                    <button
                        type="button"
                        className="event-evaluation-primary"
                        onClick={saveRequirements}
                        disabled={savingRequirements}
                    >
                        {savingRequirements
                            ? "Saving..."
                            : "Save Requirements"}
                    </button>

                </div>

            </section>

            {/* =================================================
                EVALUATION CRITERIA
            ================================================= */}

            <section className="event-evaluation-card event-evaluation-criteria-section">

                <div className="event-evaluation-section-heading">

                    <span className="event-evaluation-section-icon">
                        02
                    </span>

                    <div>
                        <h2>Evaluation Criteria</h2>

                        <p>
                            Define how applicants should be
                            scored for this event.
                        </p>
                    </div>
                </div>

                <div className="event-evaluation-criteria-toolbar">

                    <div>
                        <span className="event-evaluation-toolbar-label">
                            SCORING MODEL
                        </span>

                        <p>
                            Add weighted criteria that will later
                            drive objective evaluation and ranking.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="event-evaluation-secondary"
                        onClick={openCreateCriterion}
                    >
                        + Add Criterion
                    </button>

                </div>

                <div className="event-evaluation-criteria-list">

                    {criteria.length === 0 ? (
                        <div className="event-evaluation-empty">

                            <div className="event-evaluation-empty-icon">
                                +
                            </div>

                            <h3>
                                No evaluation criteria yet
                            </h3>

                            <p>
                                Add your first scoring criterion
                                to begin configuring candidate
                                evaluation.
                            </p>

                        </div>
                    ) : (
                        criteria.map((criterion, index) => (
                            <article
                                key={criterion.id}
                                className="event-evaluation-criterion"
                            >

                                <div className="event-evaluation-criterion-index">
                                    {String(index + 1).padStart(2, "0")}
                                </div>

                                <div className="event-evaluation-criterion-main">

                                    <div className="event-evaluation-criterion-top">

                                        <div>
                                            <h3>
                                                {criterion.name}
                                            </h3>

                                            {criterion.description && (
                                                <p>
                                                    {criterion.description}
                                                </p>
                                            )}
                                        </div>

                                        <strong className="event-evaluation-criterion-weight">
                                            {criterion.weight}%
                                        </strong>

                                    </div>

                                    <div className="event-evaluation-criterion-meta">

                                        <span>
                                            {criterion.criterionType}
                                        </span>

                                        <span>
                                            Score{" "}
                                            {criterion.minScore}
                                            {" – "}
                                            {criterion.maxScore}
                                        </span>

                                        <span
                                            className={
                                                criterion.enabled
                                                    ? "criterion-enabled"
                                                    : "criterion-disabled"
                                            }
                                        >
                                            <span className="criterion-dot" />

                                            {criterion.enabled
                                                ? "Enabled"
                                                : "Disabled"}
                                        </span>

                                    </div>

                                    <div className="event-evaluation-criterion-actions">

                                        <button
                                            type="button"
                                            className="criterion-action-edit"
                                            onClick={() =>
                                                openEditCriterion(
                                                    criterion
                                                )
                                            }
                                        >
                                            Edit
                                        </button>

                                        <button
                                            type="button"
                                            className="criterion-action-delete"
                                            onClick={() =>
                                                deleteCriterion(
                                                    criterion
                                                )
                                            }
                                            disabled={
                                                deletingCriterionId ===
                                                criterion.id
                                            }
                                        >
                                            {deletingCriterionId ===
                                            criterion.id
                                                ? "Deleting..."
                                                : "Delete"}
                                        </button>

                                    </div>

                                </div>

                            </article>
                        ))
                    )}

                </div>

                                <div
                    className={
                        "event-evaluation-readiness " +
                        (
                            evaluationReady
                                ? "ready"
                                : "incomplete"
                        )
                    }
                >

                    <div className="event-evaluation-readiness-top">

                        <div>
                            <span className="event-evaluation-weight-label">
                                EVALUATION READINESS
                            </span>

                            <strong className="event-evaluation-readiness-title">
                                {evaluationReady
                                    ? "Ready for Evaluation"
                                    : "Configuration Incomplete"}
                            </strong>

                            <p>
                                {evaluationReady
                                    ? "All active criteria weights total exactly 100%."
                                    : "Active evaluation criteria must total exactly 100% before evaluation can begin."}
                            </p>
                        </div>

                        <div className="event-evaluation-readiness-score">

                            <strong>
                                {activeWeight.toFixed(0)}%
                            </strong>

                            <span>
                                / 100%
                            </span>

                        </div>

                    </div>

                    <div className="event-evaluation-progress">

                        <div
                            className={
                                evaluationReady
                                    ? "event-evaluation-progress-fill ready"
                                    : "event-evaluation-progress-fill"
                            }
                            style={{
                                width: `${weightPercentage}%`
                            }}
                        />

                    </div>

                    <div className="event-evaluation-summary">

                        <span>
                            <strong>
                                {criteria.length}
                            </strong>
                            {" "}criteria
                        </span>

                        <span>
                            <strong>
                                {enabledCriteriaCount}
                            </strong>
                            {" "}active
                        </span>

                        <span>
                            <strong>
                                {disabledCriteriaCount}
                            </strong>
                            {" "}disabled
                        </span>

                        <span>
                            <strong>
                                {activeWeight.toFixed(0)}%
                            </strong>
                            {" "}active weight
                        </span>

                    </div>

                    <div className="event-evaluation-readiness-state">

                        <span className="weight-state-icon">
                            {evaluationReady ? "✓" : "!"}
                        </span>

                        <span>
                            {evaluationReady
                                ? "AthletiQ can evaluate and rank applicants."
                                : "Adjust criterion weights or enable/disable criteria to reach 100%."}
                        </span>

                    </div>

                </div>

            </section>

            {/* =================================================
                ADD / EDIT CRITERION MODAL
            ================================================= */}

            {showCriterionModal && (
                <div
                    className="event-evaluation-modal-backdrop"
                    role="presentation"
                    onMouseDown={event => {
                        if (
                            event.target ===
                                event.currentTarget &&
                            !savingCriterion
                        ) {
                            closeCriterionModal();
                        }
                    }}
                >

                    <section
                        className="event-evaluation-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="criterion-modal-title"
                    >

                        <div className="event-evaluation-modal-header">

                            <div>

                                <span className="event-evaluation-eyebrow">
                                    SCORING MODEL
                                </span>

                                <h2 id="criterion-modal-title">
                                    {editingCriterionId !== null
                                        ? "Edit Evaluation Criterion"
                                        : "Add Evaluation Criterion"}
                                </h2>

                                <p>
                                    Define one scoring dimension
                                    for this event.
                                </p>

                            </div>

                            <button
                                type="button"
                                className="event-evaluation-modal-close"
                                onClick={closeCriterionModal}
                                disabled={savingCriterion}
                                aria-label="Close"
                            >
                                ×
                            </button>

                        </div>

                        {criterionError && (
                            <div className="event-evaluation-alert error">
                                <strong>
                                    Unable to save
                                </strong>

                                <span>
                                    {criterionError}
                                </span>
                            </div>
                        )}

                        <div className="event-evaluation-modal-grid">

                            <label className="event-evaluation-field full">
                                <span>Criterion Name</span>

                                <input
                                    value={criterionForm.name}
                                    onChange={event =>
                                        updateCriterion(
                                            "name",
                                            event.target.value
                                        )
                                    }
                                    placeholder="e.g. Recent Form"
                                    autoFocus
                                />
                            </label>

                            <label className="event-evaluation-field full">
                                <span>Description</span>

                                <textarea
                                    value={
                                        criterionForm.description
                                    }
                                    onChange={event =>
                                        updateCriterion(
                                            "description",
                                            event.target.value
                                        )
                                    }
                                    placeholder={
                                        "Describe what this criterion measures"
                                    }
                                />
                            </label>

                            <label className="event-evaluation-field">
                                <span>Weight (%)</span>

                                <input
                                    type="number"
                                    min="0.01"
                                    max="100"
                                    step="0.01"
                                    value={criterionForm.weight}
                                    onChange={event =>
                                        updateCriterion(
                                            "weight",
                                            event.target.value
                                        )
                                    }
                                    placeholder="10"
                                />
                            </label>

                            <label className="event-evaluation-field">
                                <span>Criterion Type</span>

                                <select
                                    value={
                                        criterionForm.criterionType
                                    }
                                    onChange={event =>
                                        updateCriterion(
                                            "criterionType",
                                            event.target.value
                                        )
                                    }
                                >
                                    <option value="NUMERIC">
                                        Numeric
                                    </option>

                                    <option value="RATING">
                                        Rating
                                    </option>

                                    <option value="BOOLEAN">
                                        Boolean
                                    </option>

                                    <option value="TEXT_ASSESSMENT">
                                        Text Assessment
                                    </option>

                                </select>
                            </label>

                            <label className="event-evaluation-field">
                                <span>Minimum Score</span>

                                <input
                                    type="number"
                                    value={
                                        criterionForm.minScore
                                    }
                                    onChange={event =>
                                        updateCriterion(
                                            "minScore",
                                            event.target.value
                                        )
                                    }
                                />
                            </label>

                            <label className="event-evaluation-field">
                                <span>Maximum Score</span>

                                <input
                                    type="number"
                                    value={
                                        criterionForm.maxScore
                                    }
                                    onChange={event =>
                                        updateCriterion(
                                            "maxScore",
                                            event.target.value
                                        )
                                    }
                                />
                            </label>

                            <label className="event-evaluation-toggle">

                                <input
                                    type="checkbox"
                                    checked={
                                        Boolean(
                                            criterionForm.enabled
                                        )
                                    }
                                    onChange={event =>
                                        updateCriterion(
                                            "enabled",
                                            event.target.checked
                                        )
                                    }
                                />

                                <span>

                                    <strong>
                                        Enable criterion
                                    </strong>

                                    <small>
                                        Enabled criteria contribute
                                        to the active 100% weight.
                                    </small>

                                </span>

                            </label>

                        </div>

                        <div className="event-evaluation-modal-footer">

                            <button
                                type="button"
                                className="event-evaluation-secondary"
                                onClick={closeCriterionModal}
                                disabled={savingCriterion}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="event-evaluation-primary"
                                onClick={saveCriterion}
                                disabled={savingCriterion}
                            >
                                {savingCriterion
                                    ? "Saving..."
                                    : editingCriterionId !== null
                                        ? "Update Criterion"
                                        : "Add Criterion"}
                            </button>

                        </div>

                    </section>

                </div>
            )}

        </main>
    );
}