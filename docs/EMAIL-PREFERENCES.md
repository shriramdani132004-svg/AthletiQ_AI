# Email Preferences API

## Endpoints

GET /api/v1/profile/email-preferences

PUT /api/v1/profile/email-preferences

## Preferences

- eventUpdates
- selectionUpdates
- marketingEmails

## Security

Preferences are resolved using the authenticated user identity. Client-supplied user IDs are not accepted.