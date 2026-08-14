package com.athletiq.backend.profile.service;

import com.athletiq.backend.profile.dto.EmailPreferencesRequest;
import com.athletiq.backend.profile.dto.EmailPreferencesResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailPreferencesService {

    private final Map<String, EmailPreferencesResponse> preferences = new ConcurrentHashMap<>();

    public EmailPreferencesResponse getPreferences(String email) {
        return preferences.getOrDefault(email.toLowerCase(), new EmailPreferencesResponse(true, true, false));
    }

    public EmailPreferencesResponse updatePreferences(String email, EmailPreferencesRequest request) {
        EmailPreferencesResponse response = new EmailPreferencesResponse(request.eventUpdates(), request.selectionUpdates(), request.marketingEmails());
        preferences.put(email.toLowerCase(), response);
        return response;
    }
}