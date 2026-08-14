package com.athletiq.backend.event.service;

import com.athletiq.backend.event.entity.EventStatus;

public final class EventLifecycle {

    private EventLifecycle() {
    }

    public static boolean canTransition(EventStatus current, EventStatus target) {
        if (current == null || target == null) {
            return false;
        }

        return switch (current) {
            case DRAFT -> target == EventStatus.PUBLISHED;
            case PUBLISHED -> target == EventStatus.APPLICATIONS_OPEN || target == EventStatus.ARCHIVED;
            case APPLICATIONS_OPEN -> target == EventStatus.APPLICATIONS_CLOSED;
            case APPLICATIONS_CLOSED -> target == EventStatus.APPLICATIONS_OPEN || target == EventStatus.SELECTION || target == EventStatus.ARCHIVED;
            case SELECTION -> target == EventStatus.COMPLETED;
            case COMPLETED -> target == EventStatus.ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}