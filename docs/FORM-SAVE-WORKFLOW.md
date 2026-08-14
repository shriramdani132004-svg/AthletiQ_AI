# FORM SAVE WORKFLOW

## Objective

Persist changes made to a DRAFT form version only after validation.

## Save Flow

DRAFT VERSION
  -> Check editable state
  -> Validate fields
  -> Prepare payload
  -> Persist fields
  -> Return save result

## Validation Gate

Invalid field configuration prevents persistence.

## Immutable Versions

PUBLISHED and ARCHIVED versions cannot be saved.

## Save Payload

The save workflow prepares:

- formVersionId
- fields

## Architecture

Form Builder
  -> formSaveWorkflow.js
  -> Validation
  -> Field persistence operation
  -> Backend API

## Error Handling

The workflow returns validation errors before persistence.

Persistence failures are propagated to the caller.

## Safety Rule

Saving a form never mutates a published or archived version.