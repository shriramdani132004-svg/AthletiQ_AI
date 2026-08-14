# FORM BUILDER UI

## Components

- FormBuilderPage.jsx
- FormFieldCard.jsx
- FormPreview.jsx

## Supported Operations

- Create form
- Create form version
- Select form version
- Add field
- Edit field
- Delete field
- Duplicate field
- Publish version
- Preview fields

## Editing Rule

Only DRAFT form versions expose editing operations.

Published and locked versions remain read-only.

## UI Flow

Organizer
  -> Form Builder
  -> Select Form Version
  -> Add / Edit / Duplicate / Delete Field
  -> Preview Form
  -> Publish Version
  -> Immutable Form Version