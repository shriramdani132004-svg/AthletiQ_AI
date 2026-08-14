# FORM SECURITY CHAIN

## Required Security Order

Authentication
-> Role
-> Permission
-> Event Ownership
-> Form Ownership
-> Form Version Access
-> Operation

## Phase 5 Rule

Every protected form operation must pass the complete authorization chain before mutation or publication.

## Version Safety

DRAFT versions may be modified when authorization succeeds.
PUBLISHED and ARCHIVED versions remain immutable.

## Verification

The Phase 5 security audit verifies authentication, role, permission, event ownership, form ownership, form-version access, and protected operation handling.