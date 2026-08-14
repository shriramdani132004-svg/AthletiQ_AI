# Real-Time Requirements

## Technology Direction

WebSocket or Server-Sent Events.

## Goal

Organizer dashboard changes appear automatically without full-page refresh.

## Required Events

- APPLICATION_CREATED
- APPLICATION_UPDATED
- EVALUATION_STARTED
- EVALUATION_COMPLETED
- RANKING_UPDATED
- CANDIDATE_SHORTLISTED
- CANDIDATE_SELECTED
- EMAIL_SENT
- CANDIDATE_ACCEPTED
- CANDIDATE_DECLINED
- CANDIDATE_REMOVED
- STATUS_CHANGED

## Example

499 Applications
→ Player submits
→ 500 Applications

Dashboard updates automatically.

## Consumers

Primary:
- Organizer dashboard.

Potential future:
- Staff workspace.
- Selected-player workspace.

## Reliability

Database is the source of truth.

Real-time events notify clients about persisted changes.

If the live connection fails, clients retrieve current state through normal APIs.
