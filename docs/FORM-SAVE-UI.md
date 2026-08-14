# FORM SAVE UI

## Save States

IDLE = no pending changes
DIRTY = unsaved changes exist
SAVING = persistence operation in progress
SAVED = latest changes persisted
ERROR = persistence failed

## Save Button

The Save Draft button is enabled only when:

- The selected version is editable
- The form has unsaved changes
- A save operation is not already running

## Immutable Versions

Published and archived versions cannot be saved.

## UI Flow

Editing -> DIRTY -> Save Draft -> SAVING -> SAVED

Failure:

SAVING -> ERROR

## Safety

The save UI delegates version permission to the existing workspace and save workflow guards.