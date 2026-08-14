# AthletiQ Permission Model

## Roles

- SUPER_ADMIN
- ORGANIZER
- STAFF
- PLAYER

## Permission Matrix

| Permission | SUPER_ADMIN | ORGANIZER | STAFF | PLAYER |
|---|:---:|:---:|:---:|:---:|
| EVENT_CREATE | ✓ | ✓ | | |
| EVENT_READ | ✓ | ✓ | ✓ | ✓ |
| EVENT_UPDATE | ✓ | ✓ | | |
| EVENT_DELETE | ✓ | ✓ | | |
| APPLICATION_CREATE | ✓ | | | ✓ |
| APPLICATION_READ | ✓ | ✓ | ✓ | |
| APPLICATION_READ_OWN | ✓ | | | ✓ |
| APPLICATION_UPDATE_OWN | ✓ | | | ✓ |
| APPLICATION_EVALUATE | ✓ | ✓ | ✓ | |
| APPLICATION_SHORTLIST | ✓ | ✓ | | |
| PLAYER_SELECT | ✓ | ✓ | | |
| STAFF_MANAGE | ✓ | ✓ | | |
| EVENT_DASHBOARD_READ | ✓ | ✓ | ✓ | |

## Authorization Order

Authentication
      ↓
Role
      ↓
Permission
      ↓
Resource Ownership / Access

## Rule

Permissions do not replace resource ownership checks.

An ORGANIZER with EVENT_UPDATE must still own or have access to the specific Event.

A PLAYER with APPLICATION_READ_OWN may only access their own application.
