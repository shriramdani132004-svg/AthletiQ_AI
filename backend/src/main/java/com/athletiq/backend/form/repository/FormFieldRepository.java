package com.athletiq.backend.form.repository;

import com.athletiq.backend.form.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormFieldRepository extends JpaRepository<FormField, Long> {

    List<FormField> findByFormVersionIdOrderByDisplayOrderAsc(
            Long formVersionId
    );

    Optional<FormField> findByFormVersionIdAndFieldKey(
            Long formVersionId,
            String fieldKey
    );

    boolean existsByFormVersionIdAndFieldKey(
            Long formVersionId,
            String fieldKey
    );

    boolean existsByFormVersionIdAndDisplayOrder(
            Long formVersionId,
            Integer displayOrder
    );
}