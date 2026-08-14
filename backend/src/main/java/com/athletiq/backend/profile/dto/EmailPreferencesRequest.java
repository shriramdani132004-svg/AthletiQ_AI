package com.athletiq.backend.profile.dto;

public record EmailPreferencesRequest(
        boolean eventUpdates,
        boolean selectionUpdates,
        boolean marketingEmails
) {
}