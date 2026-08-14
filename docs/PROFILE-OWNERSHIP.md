# Profile Ownership Rules

## Organizer Profile

Every profile operation uses the authenticated user identity as the resource owner.

## Rules

- A user may read their own profile.
- A user may update their own profile.
- A user may not update another user profile.
- Profile ownership is independent of role.

## Security Flow

Authentication -> User ID -> Profile Ownership -> Permission -> Allow / Deny