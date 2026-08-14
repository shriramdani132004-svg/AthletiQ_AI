# Organizer Profile API

## GET /api/v1/profile

Returns the authenticated user profile.

## PUT /api/v1/profile

Updates the authenticated user profile.

## Security

The authenticated identity is used to resolve the profile owner. Client-supplied user IDs are not trusted for ownership.