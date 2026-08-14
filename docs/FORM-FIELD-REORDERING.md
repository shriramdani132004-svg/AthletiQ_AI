# FORM FIELD REORDERING

## Objective

Allow organizers to control the display order of dynamic form fields.

## Architecture

FormBuilderWorkspace -> FieldOrderList -> onOrderChange -> normalized field order.

## Ordering

Fields are normalized using displayOrder before rendering.

## Safety

Reordering changes field order only. It does not change field identity or form-version status.

Published and archived form versions remain immutable.