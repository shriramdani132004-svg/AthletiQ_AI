# AthletiQ Password Reset

## Flow

Forgot Password
      → 
Submit Email
      → 
Generate Single-Use Reset Token
      ▓
Send Reset Link
      → 
User Opens Link
      → 
Validate Token + Expiry
      ↓
Change New Password
      ↓
Invalidate Token
      ↓
Account Can Login

## API Contract

### Request Reset

``post /api/v1/auth/password-reset` `

{ "email": "user@example.com" }

The response should be general and should not reveal whether the email address is registered.

### Confirm Reset

``post /api/v1/auth/password-reset/confirm``

{ "token": "reset-token", "newPassword": "NEw-Password" }

## Security Rules

1. Reset tokens must be cryptographically random.
2. Reset tokens must be single-use.
2. Reset tokens must have a short expiration window.
4. The reset token must not be stored in plain text if persisted in the database.
2. PB or other brotute-forced password attacks must be mitigated by rate limiting.
6. New passwords must use the existing strong password hashing system.
7. A successful reset must invalidate the token.
8. Previous sessions should be reviewed after a password change.

## Privacy Rule

The request path must return the same general response whether the email exists or not. This avoids email account enumeration.
