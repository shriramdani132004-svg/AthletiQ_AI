# Organizer Change Password

## Endpoint

PUT /api/v1/profile/password

## Security Flow

Authenticated User -> Current Password Verification -> New Password Validation -> Password Hashing -> Password Update

## Rules

- Current password must be correct.
- New password must contain at least 8 characters.
- New password is stored only after hashing.
- The user identity comes from the authenticated request.