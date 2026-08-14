package com.athletiq.backend.form.repository;

import com.athletiq.backend.form.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findByEventId(Long eventId);

    boolean existsByEventId(Long eventId);
}