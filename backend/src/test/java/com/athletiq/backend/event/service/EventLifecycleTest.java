package com.athletiq.backend.event.service;

import com.athletiq.backend.event.entity.EventStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventLifecycleTest {

    @Test
    void draftCanBePublished() {
        assertTrue(EventLifecycle.canTransition(EventStatus.DRAFT, EventStatus.PUBLISHED));
    }

    @Test
    void publishedCanOpenApplications() {
        assertTrue(EventLifecycle.canTransition(EventStatus.PUBLISHED, EventStatus.APPLICATIONS_OPEN));
    }

    @Test
    void openApplicationsCanBeClosed() {
        assertTrue(EventLifecycle.canTransition(EventStatus.APPLICATIONS_OPEN, EventStatus.APPLICATIONS_CLOSED));
    }

    @Test
    void closedApplicationsCanBeReopened() {
        assertTrue(EventLifecycle.canTransition(EventStatus.APPLICATIONS_CLOSED, EventStatus.APPLICATIONS_OPEN));
    }

    @Test
    void closedApplicationsCanEnterSelection() {
        assertTrue(EventLifecycle.canTransition(EventStatus.APPLICATIONS_CLOSED, EventStatus.SELECTION));
    }

    @Test
    void selectionCanBeCompleted() {
        assertTrue(EventLifecycle.canTransition(EventStatus.SELECTION, EventStatus.COMPLETED));
    }

    @Test
    void completedCanBeArchived() {
        assertTrue(EventLifecycle.canTransition(EventStatus.COMPLETED, EventStatus.ARCHIVED));
    }

    @Test
    void archivedIsTerminal() {
        assertFalse(EventLifecycle.canTransition(EventStatus.ARCHIVED, EventStatus.DRAFT));
        assertFalse(EventLifecycle.canTransition(EventStatus.ARCHIVED, EventStatus.PUBLISHED));
        assertFalse(EventLifecycle.canTransition(EventStatus.ARCHIVED, EventStatus.APPLICATIONS_OPEN));
        assertFalse(EventLifecycle.canTransition(EventStatus.ARCHIVED, EventStatus.COMPLETED));
    }

    @Test
    void invalidTransitionsAreRejected() {
        assertFalse(EventLifecycle.canTransition(EventStatus.DRAFT, EventStatus.APPLICATIONS_OPEN));
        assertFalse(EventLifecycle.canTransition(EventStatus.DRAFT, EventStatus.COMPLETED));
        assertFalse(EventLifecycle.canTransition(EventStatus.PUBLISHED, EventStatus.COMPLETED));
        assertFalse(EventLifecycle.canTransition(EventStatus.SELECTION, EventStatus.ARCHIVED));
    }

    @Test
    void nullStatusesAreRejected() {
        assertFalse(EventLifecycle.canTransition(null, EventStatus.PUBLISHED));
        assertFalse(EventLifecycle.canTransition(EventStatus.DRAFT, null));
        assertFalse(EventLifecycle.canTransition(null, null));
    }
}