# Phase 4 Event Routes

## Routes

- /events - Organizer event management
- /events/create - Create event
- /events/:eventId - Event details and lifecycle controls
- /events/:eventId/edit - Edit DRAFT event

## Security

Frontend routing does not replace backend authorization.

Authentication
    ↓
Role
    ↓
Permission
    ↓
Resource Ownership
    ↓
Event Operation