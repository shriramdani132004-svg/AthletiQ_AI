# FORM VERSIONING

## Version Model

Each Form owns multiple immutable Form Versions.

`	ext
Form
 ├── Form Version 1
 ├── Form Version 2
 └── Form Version 3
`",
    ",
    

`	ext
DRAFT
  ↓
PUBLISHED
  ↓
IMMUTABLE
`",
    ",
    

Only DRAFT versions can be edited.

Published and archived versions are immutable.

## Creating a New Version

When a published form needs modification:

1. Keep the published version unchanged.
2. Create a new version number.
3. Set the new version to DRAFT.
4. Record the published version as sourceVersionId.
5. Modify the new draft.
6. Validate the draft.
7. Publish the new version.

## Historical Safety

Existing applications remain associated with the version that was active when the application was submitted.