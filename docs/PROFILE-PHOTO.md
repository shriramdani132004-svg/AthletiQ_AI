# Profile Photo API

## Endpoints

GET /api/v1/profile/photo

PUT /api/v1/profile/photo

## Current Scope

Phase 3 stores a validated profile photo URL associated with the authenticated user.

Binary upload, object storage, image processing, resizing, and CDN delivery are future infrastructure concerns.

## Security

The profile owner is resolved from the authenticated identity. Client-supplied user IDs are not accepted.