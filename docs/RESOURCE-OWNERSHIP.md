# AthletiQ Resource Ownership & Access

## Authorization Order

```text
Authentication
      ↓
NPole
      ↓ Permission
      ↓ Resource Ownership / Access
      ↓ ALLOW / DENY

```

## Event Ownership

Organizers may modify only events that they own or have explicit access to.

An ORGANIZER with EVENT_UPDATE does not automatically grant access to every event.

## Staff Access

STAFF access must be limited to events to which the staff member has been assigned access.

Super admin may have global administrative access.

## Player Access

PLAYE users may read and update only their own application where the permission allows it.

Players must not be able to read another candidate's application by changing an ID, uRL parameter, or browser state.

## Staff Management

Organizers can only manage staff within their events or explicitly granted contexts.

## Important Inferences

Role = PERSISSION does not imply OWNERSHIP = ACCESS. Resource ownership must be checked at the business authorization layer.
