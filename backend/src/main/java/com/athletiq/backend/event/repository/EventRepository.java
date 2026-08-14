package com.athletiq.backend.event.repository;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByOrganizerIdAndStatus(Long organizerId, EventStatus status);

    Optional<Event> findByIdAndOrganizerId(Long id, Long organizerId);
}