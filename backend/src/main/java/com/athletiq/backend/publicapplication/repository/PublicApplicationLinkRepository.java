package com.athletiq.backend.publicapplication.repository;

import com.athletiq.backend.publicapplication.entity.PublicApplicationLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicApplicationLinkRepository
        extends JpaRepository<PublicApplicationLink, Long> {

    Optional<PublicApplicationLink> findByEventId(Long eventId);

    Optional<PublicApplicationLink> findByPublicCode(String publicCode);

    boolean existsByEventId(Long eventId);

    boolean existsByPublicCode(String publicCode);
}