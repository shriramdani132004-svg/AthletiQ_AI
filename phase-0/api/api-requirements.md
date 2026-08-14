# AthletiQ API Requirements

## API Style

Spring Boot REST APIs are the primary backend interface.

## Authentication

POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh
POST /api/auth/verify-email
POST /api/auth/forgot-password
POST /api/auth/reset-password

## Users

GET /api/users/me
PUT /api/users/me
PUT /api/users/me/password

## Events

POST /api/events
GET /api/events
GET /api/events/{eventId}
PUT /api/events/{eventId}
POST /api/events/{eventId}/publish
POST /api/events/{eventId}/pause
POST /api/events/{eventId}/reopen
POST /api/events/{eventId}/close
POST /api/events/{eventId}/archive
POST /api/events/{eventId}/duplicate

## Forms

POST /api/events/{eventId}/forms
GET /api/events/{eventId}/forms
GET /api/forms/{formId}
PUT /api/forms/{formId}
POST /api/forms/{formId}/publish
POST /api/forms/{formId}/preview

## Public Application

GET /api/public/events/{eventId}
GET /api/public/events/{eventId}/form
POST /api/public/events/{eventId}/applications

## Applications

GET /api/events/{eventId}/applications
GET /api/applications/{applicationId}
PUT /api/applications/{applicationId}
POST /api/applications/{applicationId}/validate
POST /api/applications/{applicationId}/shortlist
POST /api/applications/{applicationId}/select
POST /api/applications/{applicationId}/reject
POST /api/applications/{applicationId}/remove

## Evaluation

POST /api/applications/{applicationId}/evaluate
GET /api/applications/{applicationId}/evaluation
POST /api/events/{eventId}/evaluate

## Ranking

GET /api/events/{eventId}/rankings
GET /api/events/{eventId}/rankings/{applicationId}

## Communication

POST /api/applications/{applicationId}/email
POST /api/events/{eventId}/email/bulk
GET /api/applications/{applicationId}/emails

## Candidate Response

POST /api/selections/{selectionId}/accept
POST /api/selections/{selectionId}/decline

## Selected Players

GET /api/events/{eventId}/selected
POST /api/selections/{selectionId}/finalize
POST /api/selections/{selectionId}/reminder
POST /api/selections/{selectionId}/resend

## API Rules

- Validate request data.
- Authenticate protected requests.
- Authorize by role and permission.
- Verify ownership/access.
- Return consistent errors.
- Never trust client-side status transitions.
- Paginate large collections.
