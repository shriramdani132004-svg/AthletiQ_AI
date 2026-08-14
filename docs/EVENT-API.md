# Phase 4 Event REST API

## Organizer Event APIs

POST /api/v1/events
Create a new event in DRAFT status.

GET /api/v1/events
List events owned by the authenticated organizer.

GET /api/v1/events/{eventId}
Read an event owned by the authenticated organizer.

PUT /api/v1/events/{eventId}
Update an owned DRAFT event.

POST /api/v1/events/{eventId}/publish
Transition DRAFT to PUBLISHED.

POST /api/v1/events/{eventId}/applications/open
Open applications after publication.

POST /api/v1/events/{eventId}/applications/pause
Pause applications by transitioning to APPLICATIONS_CLOSED.

POST /api/v1/events/{eventId}/applications/reopen
Reopen applications by transitioning to APPLICATIONS_OPEN.

POST /api/v1/events/{eventId}/applications/close
Close applications.

POST /api/v1/events/{eventId}/archive
Archive an event when the lifecycle permits it.

POST /api/v1/events/{eventId}/duplicate
Create a new DRAFT copy of an owned event.

## Security

The authenticated principal supplies the organizer identity. Event IDs supplied by the client are always resolved together with the authenticated organizer ID.