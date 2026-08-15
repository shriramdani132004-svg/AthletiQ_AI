package com.athletiq.backend.event.requirements.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import com.athletiq.backend.event.requirements.dto.EventRequirementsRequest;
import com.athletiq.backend.event.requirements.dto.EventRequirementsResponse;
import com.athletiq.backend.event.requirements.entity.EventRequirements;
import com.athletiq.backend.event.requirements.repository.EventRequirementsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRequirementsServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventRequirementsRepository requirementsRepository;

    @InjectMocks
    private EventRequirementsService service;

    @Test
    void getReturnsEmptyResponseWhenRequirementsDoNotExist() {
        Event event = event(1L);

        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(requirementsRepository.findByEventId(1L))
                .thenReturn(Optional.empty());

        EventRequirementsResponse response =
                service.get(5L, 1L);

        assertEquals(1L, response.eventId());
        assertNull(response.id());
    }

    @Test
    void updateCreatesRequirementsForOwnedEvent() {
        Event event = event(1L);

        EventRequirementsRequest request =
                new EventRequirementsRequest(
                        "Batsman, All-rounder",
                        18,
                        25,
                        "2 years",
                        "District level",
                        "Batting, fielding",
                        "Strong recent form",
                        "Team fitness",
                        "Available throughout event",
                        "Open eligibility",
                        "Sports academy preferred"
                );

        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        when(requirementsRepository.findByEventId(1L))
                .thenReturn(Optional.empty());

        when(requirementsRepository.save(any(EventRequirements.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventRequirementsResponse response =
                service.update(5L, 1L, request);

        assertEquals(1L, response.eventId());
        assertEquals(18, response.minAge());
        assertEquals(25, response.maxAge());
        assertEquals(
                "Batsman, All-rounder",
                response.requiredPositions()
        );

        verify(requirementsRepository).save(any(EventRequirements.class));
    }

    @Test
    void invalidAgeRangeIsRejected() {
        Event event = event(1L);

        EventRequirementsRequest request =
                new EventRequirementsRequest(
                        "Batsman",
                        30,
                        20,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        when(eventRepository.findByIdAndOrganizerId(1L, 5L))
                .thenReturn(Optional.of(event));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.update(5L, 1L, request)
                );

        assertEquals(
                "Maximum age cannot be less than minimum age.",
                exception.getMessage()
        );

        verify(requirementsRepository, never())
                .save(any(EventRequirements.class));
    }

    @Test
    void nonOwnerCannotAccessRequirements() {
        when(eventRepository.findByIdAndOrganizerId(1L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.get(999L, 1L)
        );

        verifyNoInteractions(requirementsRepository);
    }

    private Event event(Long id) {
        Event event = new Event();
        event.setId(id);
        event.setOrganizerId(5L);
        event.setName("Test Event");
        event.setSport("Football");
        return event;
    }
}