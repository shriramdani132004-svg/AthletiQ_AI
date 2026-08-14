# FORM VERSION MANAGEMENT UI

## Supported Version States

- DRAFT - editable
- PUBLISHED - immutable
- ARCHIVED - immutable

The selector renders the status dynamically from each version object.

The selector does not hard-code every status into its rendering logic.

## Immutable Versions

Published and archived versions are displayed as read-only.

## New Draft

A new draft can be created from a published version.

The published version remains unchanged.