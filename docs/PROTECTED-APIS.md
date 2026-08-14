# AthletiQ Protected API Policy

## Protected Endpoints

| Endpoint | Required Role |
|---|---|
| GET /api/v1/admin/dashboard | SUPER_ADMIN |
| GET /api/v1/organizer/profile | SUPER_ADMIN, ORGANIZER |
| GET /api/v1/staff/dashboard | SUPER_ADMIN, ORGANIZER, STAFF |
| GET /api/v1/player/profile | SUPER_ADMIN, ORGANIZER, STAFF, PLAYER |

## Security Flow

Request
  ↓
JWT authentication
  ↓
Authenticated user
  ↓
Role authorization
  ↓
Permission authorization
  ↓
Resource ownership/access
  ↓
Controller

## HTTP Responses

401 Unauthorized = missing or invalid authentication
403 Forbidden = authenticated but insufficient authorization
200 OK = authenticated and authorized

## Security Rule

Every protected business operation must verify authentication, role, permission, and resource ownership/access before execution.
