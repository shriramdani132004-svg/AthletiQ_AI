# FORM PREVIEW API

## Objective

Render the selected FormVersion using its persisted field configuration without modifying persisted form data.

## Backend Data Source

GET /api/events/{eventId}/form/versions/{versionId}/fields

## Frontend

FormPreview.jsx accepts the fields collection and renders the configured field types.

## Read-Only Safety

Preview does not persist changes. The preview submit control is disabled and uses type=button.

PUBLISHED and ARCHIVED versions remain immutable.