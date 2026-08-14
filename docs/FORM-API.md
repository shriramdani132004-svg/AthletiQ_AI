# FORM BUILDER API

Base path:

`/api/events/{eventId}/form`

## Form

### Create Form

`POST /api/events/{eventId}/form?organizerId={organizerId}`

### Get Form

`GET /api/events/{eventId}/form?organizerId={organizerId}`

## Form Versions

### List Versions

`GET /api/events/{eventId}/form/versions?organizerId={organizerId}`

### Create Version

`POST /api/events/{eventId}/form/versions?organizerId={organizerId}`

### Clone Version

`POST /api/events/{eventId}/form/versions/{versionId}/clone?organizerId={organizerId}`

### Publish Version

`POST /api/events/{eventId}/form/versions/{versionId}/publish?organizerId={organizerId}`

## Form Fields

### List Fields

`GET /api/events/{eventId}/form/versions/{versionId}/fields?organizerId={organizerId}`

### Get Field

`GET /api/events/{eventId}/form/versions/{versionId}/fields/{fieldId}?organizerId={organizerId}`

### Add Field

`POST /api/events/{eventId}/form/versions/{versionId}/fields?organizerId={organizerId}`

### Update Field

`PUT /api/events/{eventId}/form/versions/{versionId}/fields/{fieldId}?organizerId={organizerId}`

### Delete Field

`DELETE /api/events/{eventId}/form/versions/{versionId}/fields/{fieldId}?organizerId={organizerId}`

### Duplicate Field

`POST /api/events/{eventId}/form/versions/{versionId}/fields/{fieldId}/duplicate?organizerId={organizerId}`

## Rules

- Only the event owner may manage its form.
- Only draft form versions may be modified.
- Published versions are immutable.
- Historical form versions remain available.
- Fields belong to exactly one form version.
- Field validation is enforced by the backend service.