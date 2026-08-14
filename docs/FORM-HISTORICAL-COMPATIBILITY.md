# FORM HISTORICAL COMPATIBILITY

## Rule

Every application stores the exact FormVersion used when the application was created.

An application never resolves its form through the latest published version.

## Compatibility Model

Application -> Event -> Exact FormVersion

## Guarantee

Publishing a newer form version does not replace the FormVersion reference stored by an existing application.

## Validation

The application service verifies that the selected FormVersion belongs to the requested Event before persistence.

## Historical Queries

Applications can be retrieved by their exact FormVersion using findByFormVersionId.
