package com.athletiq.backend.profile.dto;

public record EmailPreferencesResponse(
        boolean eventUpdates,
        boolean selectionUpdates,
        boolean marketingEmails
) {
}