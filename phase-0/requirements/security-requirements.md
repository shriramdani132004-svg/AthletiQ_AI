# Security Requirements

## Authentication

Protected operations require authenticated users.

## Authorization

Authentication
→ Role
→ Permission
→ Resource Ownership / Access

## Required Security Areas

- Password hashing.
- Email verification.
- Password reset.
- JWT authentication.
- Refresh token/session handling.
- Protected frontend routes.
- Protected backend APIs.
- Organizer ownership validation.
- Staff permissions.
- Player access rules.
- Input validation.
- Rate limiting.
- Secure candidate response tokens.
- Token expiration.
- Duplicate response protection.
- Secure file handling in later phases.

## Candidate Response Security

Accept/decline actions require:
- Secure random token.
- Candidate association.
- Event association.
- Expiration.
- Controlled or one-time use.
- Invalid-token handling.
- Duplicate-response protection.

## Security Principle

Never trust:
- Client-provided role.
- Client-provided ownership.
- Client-provided status.
- Client-provided evaluation result.

Business decisions must be validated server-side.
