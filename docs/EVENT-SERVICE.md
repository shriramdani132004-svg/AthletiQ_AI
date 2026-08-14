# Phase 4 Event Service

## Implemented

- Event creation service
- Organizer ownership lookup
- Organizer event listing
- Draft event editing
- Event date validation
- Event lifecycle transition validation
- Event duplication
- Automatic DRAFT status for newly created events

## Lifecycle Rules

DRAFT -> PUBLISHED
PUBLISHED -> APPLICATIONS_OPEN
PUBLISHED -> ARCHIVED
APPLICATIONS_OPEN -> APPLICATIONS_CLOSED
APPLICATIONS_CLOSED -> SELECTION
APPLICATIONS_CLOSED -> ARCHIVED
SELECTION -> COMPLETED
COMPLETED -> ARCHIVED

Invalid lifecycle transitions are rejected by the service layer.

## Ownership

All event mutations resolve the event using both event ID and organizer ID. An organizer therefore cannot modify another organizer's event by supplying a different event ID.