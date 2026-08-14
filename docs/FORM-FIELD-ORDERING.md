# FORM FIELD ORDERING

## Purpose

Allow organizers to change the order of fields in a draft form version.

## Behavior

- Fields are ordered by displayOrder.
- Drag-and-drop changes the order.
- New order is normalized to zero-based indexes.
- Updated displayOrder values are persisted through the field API.
- Published versions remain immutable.

## Flow

Organizer
  -> Drag Field
  -> Calculate New Order
  -> Normalize displayOrder
  -> Persist Each Field
  -> Reload Ordered Fields