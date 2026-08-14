# FORM WORKSPACE VERSION STATE

## Objective
The form builder workspace derives editing permissions from the selected form version.

## Version Rules
DRAFT = editable
PUBLISHED = read-only
ARCHIVED = read-only

## Draft Permissions
Draft versions may modify fields, save changes, validate the form, and publish the version.

## Immutable Permissions
Published and archived versions may be viewed and previewed but cannot be modified or published again.

## Architecture
Selected Form Version -> formVersionEditing.js -> formWorkspaceVersionState.js -> Form Builder Workspace

## Safety Rule
The workspace must never allow field modification when the selected version is immutable.