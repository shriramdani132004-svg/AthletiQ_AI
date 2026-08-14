package com.athletiq.backend.event.service;

import com.athletiq.backend.event.dto.CreateEventRequest;
import com.athletiq.backend.event.dto.EventResponse;
import com.athletiq.backend.event.dto.UpdateEventRequest;
import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.entity.EventStatus;
import com.athletiq.backend.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private CreateEventRequest createRequest() {
        return new CreateEventRequest(
                "Mumbai Cricket Trial",
                "Cricket",
                "Open cricket selection trial",
                "Mumbai",
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 12),
                LocalDateTime.of(2026, 10, 9, 23, 59),
                25,
                "U-21",
                "Eligible registered players",
                "Bring valid identification",
                "https://example.com/banner.jpg"
        );
    }

    private Event ownedDraft() {
        Event event = new Event();
        event.setId(10L);
        event.setOrganizerId(100L);
        event.setName("Mumbai Cricket Trial");
        event.setSport("Cricket");
        event.setStartDate(LocalDate.of(2026, 10, 10));
        event.setEndDate(LocalDate.of(2026, 10, 12));
        event.setRegistrationDeadline(LocalDateTime.of(2026, 10, 9, 23, 59));
        event.setPlayersRequired(25);
        event.setStatus(EventStatus.DRAFT);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        return event;
    }

    @Test
    void createStartsEventInDraftStatus() {
        Event saved = ownedDraft();
        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        EventResponse response = eventService.create(100L, createRequest());

        assertEquals(10L, response.id());
        assertEquals(100L, response.organizerId());
        assertEquals(EventStatus.DRAFT, response.status());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getOwnedReturnsOnlyOrganizerOwnedEvent() {
        Event event = ownedDraft();
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.getOwned(100L, 10L);

        assertEquals(10L, response.id());
        assertEquals(100L, response.organizerId());
        verify(eventRepository).findByIdAndOrganizerId(10L, 100L);
    }

    @Test
    void getOwnedRejectsMissingOrUnauthorizedEvent() {
        when(eventRepository.findByIdAndOrganizerId(10L, 999L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> eventService.getOwned(999L, 10L)
        );

        verify(eventRepository).findByIdAndOrganizerId(10L, 999L);
    }

    @Test
    void updateDraftEventSucceeds() {
        Event event = ownedDraft();
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        UpdateEventRequest request = new UpdateEventRequest(
                "Updated Trial",
                "Football",
                "Updated description",
                "Pune",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                LocalDateTime.of(2026, 11, 9, 23, 59),
                30,
                "Open",
                "Updated eligibility",
                "Updated rules",
                "https://example.com/new-banner.jpg"
        );

        EventResponse response = eventService.update(100L, 10L, request);

        assertEquals("Updated Trial", response.name());
        assertEquals("Football", response.sport());
        assertEquals(30, response.playersRequired());
        verify(eventRepository).save(event);
    }

    @Test
    void publishedEventCannotBeEdited() {
        Event event = ownedDraft();
        event.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(event));

        assertThrows(
                IllegalStateException.class,
                () -> eventService.update(100L, 10L, new UpdateEventRequest(
                        "Updated Trial",
                        "Football",
                        "Description",
                        "Pune",
                        LocalDate.of(2026, 11, 10),
                        LocalDate.of(2026, 11, 12),
                        LocalDateTime.of(2026, 11, 9, 23, 59),
                        30,
                        "Open",
                        "Eligibility",
                        "Rules",
                        null
                ))
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void invalidDatesAreRejectedDuringCreation() {
        CreateEventRequest request = new CreateEventRequest(
                "Invalid Event",
                "Cricket",
                null,
                "Mumbai",
                LocalDate.of(2026, 10, 12),
                LocalDate.of(2026, 10, 10),
                LocalDateTime.of(2026, 10, 9, 23, 59),
                10,
                "Open",
                null,
                null,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(100L, request)
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void duplicateCreatesNewDraftOwnedBySameOrganizer() {
        Event source = ownedDraft();
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(source));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event copy = invocation.getArgument(0);
            copy.setId(20L);
            return copy;
        });

        EventResponse response = eventService.duplicate(100L, 10L);

        assertEquals(20L, response.id());
        assertEquals(100L, response.organizerId());
        assertEquals("Mumbai Cricket Trial - Copy", response.name());
        assertEquals(EventStatus.DRAFT, response.status());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void publishTransitionSucceedsForOwnedDraft() {
        Event event = ownedDraft();
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse response = eventService.transition(100L, 10L, EventStatus.PUBLISHED);

        assertEquals(EventStatus.PUBLISHED, response.status());
        verify(eventRepository).save(event);
    }

    @Test
    void invalidTransitionIsRejected() {
        Event event = ownedDraft();
        when(eventRepository.findByIdAndOrganizerId(10L, 100L)).thenReturn(Optional.of(event));

        assertThrows(
                IllegalStateException.class,
                () -> eventService.transition(100L, 10L, EventStatus.SELECTION)
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void registrationDeadlineAfterStartIsRejected() {
        CreateEventRequest request = new CreateEventRequest(
                "Invalid Deadline Event",
                "Football",
                null,
                "Pune",
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 12),
                LocalDateTime.of(2026, 10, 11, 0, 0),
                20,
                "Open",
                null,
                null,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(100L, request)
        );

        verify(eventRepository, never()).save(any(Event.class));
    }
}