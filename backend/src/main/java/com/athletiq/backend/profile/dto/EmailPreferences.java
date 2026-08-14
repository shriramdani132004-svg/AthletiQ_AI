package com.athletiq.backend.profile.dto;

public record EmailPreferences(
        boolean eventUpdates,
        boolean selectionUpdates,
        boolean marketingEmails
        ) {
}