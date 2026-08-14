# Phase 4 Event Management

## Domain Foundation

The Event domain represents an organizer-owned sports event.

## Supported Event Lifecycle

DRAFT
↓
PUBLISHED
↓
APPLICATIONS_OPEN
↓
APPLICATIONS_CLOSED
↓
SELECTION
↓
COMPLETED
↓
ARCHIVED

## Event Data

- Event name
- Sport
- Description
- Location
- Start date
- End date
- Registration deadline
- Number of players required
- Age/category requirements
- Eligibility criteria
- Event rules
- Event image/banner
- Event status

## Ownership

Every event stores its organizer ID. Event ownership will be enforced by the service and authorization layers before event modification operations.