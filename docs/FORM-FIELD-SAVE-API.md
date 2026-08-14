# FORM FIELD SAVE API

## Objective

Persist edits to existing DRAFT form fields using the actual backend field-update contract.

## Actual Backend Endpoint

PUT /api/events/{eventId}/form/versions/{versionId}/fields/{fieldId}

## Request

Path parameters:

- eventId
- versionId
- fieldId

Query parameter:

- organizerId

Request body:

- FormField

## Save Flow

Form Builder
  -> Save Workflow
  -> Field Save Adapter
  -> Existing formApi field-update function
  -> PUT field endpoint
  -> FormFieldService.updateField
  -> PostgreSQL

## Draft Safety

Only editable DRAFT versions may be saved.

PUBLISHED and ARCHIVED versions remain immutable.

## Existing Fields

The adapter persists fields that already have a backend field ID.

New-field creation remains handled by the existing field-create operation.

## Important

No invented form-version PUT endpoint is used.