package com.athletiq.backend.event.requirements.repository;

import com.athletiq.backend.event.requirements.entity.EventRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRequirementsRepository
        extends JpaRepository<EventRequirements, Long> {

    Optional<EventRequirements> findByEventId(Long eventId);

    boolean existsByEventId(Long eventId);
}