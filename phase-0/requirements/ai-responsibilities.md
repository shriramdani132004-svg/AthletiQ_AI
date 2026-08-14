# AI Responsibilities and Boundaries

## AI Role

AI is an assisted evaluation component, not an uncontrolled replacement for organizer decisions.

## AI Responsibilities

AI may:
- Analyze candidate information.
- Identify strengths.
- Identify weaknesses.
- Summarize candidate profiles.
- Assess suitability against configured criteria.
- Produce an AI-assisted score/recommendation.
- Produce an explanation.

## Objective Evaluation

Objective scoring remains deterministic.

Objective and AI-assisted evaluation remain logically distinguishable.

## Organizer Responsibilities

Organizer remains responsible for:
- Requirements.
- Evaluation criteria.
- Candidate review.
- Shortlisting.
- Selection.
- Rejection.
- Finalization.

## AI Explainability

AI output should contain:
- Recommendation.
- Supporting reasoning.
- Strengths.
- Weaknesses.
- Model/version metadata where available.
- Evaluation timestamp.

## AI Failure

AI processing must not block the entire recruitment system.

AI evaluation should be asynchronous and queue-based.

## Scaling Rule

Do not perform:

500 applications
→ 500 AI calls
→ one HTTP request.

Use:

Applications
→ Queue
→ Workers
→ AI Evaluation
→ Database.
