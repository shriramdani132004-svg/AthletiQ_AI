# FORM VERSION EDITING WORKFLOW

## Objective

Provide a safe editing workflow for form versions while preserving published and archived version immutability.

## Editing Rules

Only DRAFT form versions are editable.

PUBLISHED and ARCHIVED form versions are immutable and read-only.

## Version States

DRAFT = editable
PUBLISHED = immutable
ARCHIVED = immutable

## Workflow

Select Version -> Check Status -> DRAFT: Edit -> Validate -> Save/Publish

PUBLISHED -> Read Only -> Create New Draft

ARCHIVED -> Read Only

## Published Version Modification

A published version must never be modified directly.

Create a new draft version from the published version, modify the draft, validate it, and publish the new version.

## Implementation

formVersionUtils.js
formVersionEditing.js
FormVersionSelector.jsx
FormBuilderWorkspace.jsx

## Historical Integrity

Existing published versions remain unchanged. A new version is created instead of mutating a historical version.