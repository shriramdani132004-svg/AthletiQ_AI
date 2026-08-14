# Organization Information API

## Endpoints

GET /api/v1/profile/organization

PUT /api/v1/profile/organization

## Fields

- organizationName
- organizationDescription

## Security

Organization information is resolved using the authenticated organizer identity. Client-supplied user IDs are not trusted for ownership.