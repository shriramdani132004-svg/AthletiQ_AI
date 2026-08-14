export function sortFields(fields = []) {
    return [...fields].sort(
        (a, b) => Number(a.displayOrder ?? 0) - Number(b.displayOrder ?? 0)
    );
}

export function normalizeFieldOrder(fields = []) {
    return sortFields(fields).map((field, index) => ({
        ...field,
        displayOrder: index
    }));
}

export function moveField(fields = [], fromIndex, toIndex) {
    const ordered = normalizeFieldOrder(fields);

    if (
        fromIndex < 0 ||
        toIndex < 0 ||
        fromIndex >= ordered.length ||
        toIndex >= ordered.length
    ) {
        return ordered;
    }

    const result = [...ordered];
    const [moved] = result.splice(fromIndex, 1);
    result.splice(toIndex, 0, moved);

    return normalizeFieldOrder(result);
}