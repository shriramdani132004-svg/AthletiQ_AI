# Phase 3 Profile Authorization

## Protected Profile APIs

- GET /api/v1/profile
- PUT /api/v1/profile
- PUT /api/v1/profile/password
- GET /api/v1/profile/email-preferences
- PUT /api/v1/profile/email-preferences
- GET /api/v1/profile/organization
- PUT /api/v1/profile/organization
- GET /api/v1/profile/photo
- PUT /api/v1/profile/photo

## Allowed Roles

SUPER_ADMIN and ORGANIZER.

## Ownership Rule

The authenticated identity determines the profile owner. A client cannot select another user by submitting a user ID.