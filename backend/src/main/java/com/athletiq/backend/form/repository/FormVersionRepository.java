package com.athletiq.backend.form.repository;

import com.athletiq.backend.form.entity.FormVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormVersionRepository extends JpaRepository<FormVersion, Long> {

    List<FormVersion> findByFormIdOrderByVersionNumberDesc(Long formId);

    Optional<FormVersion> findByFormIdAndVersionNumber(
            Long formId,
            Integer versionNumber
    );

    Optional<FormVersion> findTopByFormIdOrderByVersionNumberDesc(
            Long formId
    );

    boolean existsByFormIdAndVersionNumber(
            Long formId,
            Integer versionNumber
    );
}