# AthletiQ Authorization Model

## Roles

- SUPER_ADMIN
- ORGANIZER
- STAFF
- PLAYER

## Authorization Chain

Authentication
    ↓
Role
    ↓
Permission
    ↓
Resource Ownership / Access

## Route Policy

| Route | Allowed Roles |
|---|---|
| `/api/v1/admin/**` | SUPER_ADMIN |
| `/api/v1/organizer/**` | SUPER_ADMIN, ORGANIZER |
| `/api/v1/staff/**` | SUPER_ADMIN, ORGANIZER, STAFF |
| `/api/v1/player/**` | SUPER_ADMIN, ORGANIZER, STAFF, PLAYER |

## Resource Ownership

Having the correct role does not automatically grant access to every resource.

An ORGANIZER must still own an Event before modifying that Event.

Resource ownership checks are implemented separately from role checks.