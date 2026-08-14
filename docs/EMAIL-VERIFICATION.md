# AthletiQ Email Verification

## Purpose

Email verification confirms that an account is associated with a valid email address.

## Flow

Registration
    ↓
Verification Token Generated
    ↓
Verification Email
    ↓
Player / Organizer Opens Link
    ↓
Token Validation
    ↓
Account Verified

## Security Rules

- Verification tokens must expire.
- Tokens must not be reusable after successful verification.
- Invalid or expired tokens must be rejected.
- Verification status must be checked before protected account operations where required.

## Statuses

- PENDING
- VERIFIED
- EXPIRED
- REVOKED