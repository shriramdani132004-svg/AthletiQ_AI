# Phase 4 Event Lifecycle

## Lifecycle

DRAFT -> PUBLISHED
PUBLISHED -> APPLICATIONS_OPEN
PUBLISHED -> ARCHIVED
APPLICATIONS_OPEN -> APPLICATIONS_CLOSED
APPLICATIONS_CLOSED -> APPLICATIONS_OPEN
APPLICATIONS_CLOSED -> SELECTION
APPLICATIONS_CLOSED -> ARCHIVED
SELECTION -> COMPLETED
COMPLETED -> ARCHIVED

## Rules

- New events always start as DRAFT.
- Only DRAFT events may be edited.
- An organizer can only access events owned by that organizer.
- Invalid lifecycle transitions are rejected.
- ARCHIVED is terminal.
- Duplicated events always start as DRAFT.
- Registration deadline cannot be after the event start date.
- Event end date cannot be before the event start date.

## Ownership

Every event lookup used for organizer operations resolves by both event ID and authenticated organizer ID.