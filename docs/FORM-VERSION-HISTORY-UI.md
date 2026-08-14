# FORM VERSION HISTORY UI

## Objective

Display preserved form versions and allow the organizer to select a historical version for inspection.

## Version Ordering

Versions are displayed in descending version-number order.

## Status

DRAFT versions are editable when selected.
PUBLISHED versions are immutable.
ARCHIVED versions are immutable.

## Integration

FormBuilderPage owns versions, selectedVersionId, and selectVersion and passes them to FormVersionHistory.

## Safety

Selecting a historical version loads its fields but does not modify the version.
Published and archived versions remain read-only.