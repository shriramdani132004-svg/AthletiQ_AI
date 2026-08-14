# FORM SAVE AND PUBLISH SAFETY

## Save

Draft form versions may be saved after field configuration validation.

## Publish Preconditions

- Form version must exist.
- Version status must be DRAFT.
- Form must contain at least one field.
- Every field must have a valid field key.
- Field keys must be unique.
- Every field must have a valid display order.
- Field configuration must pass validation.

## Immutability

Published form versions cannot be edited.

A later modification requires creation of a new form version.

## Lifecycle

DRAFT
  -> Validate
  -> Save
  -> Publish
  -> PUBLISHED
  -> Immutable