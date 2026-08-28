package com.athletiq.backend.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.athletiq.backend.application.entity.SelectionEmailDelivery;

public interface SelectionEmailDeliveryRepository
        extends JpaRepository<
        SelectionEmailDelivery,
        Long
        > {
                long deleteByApplicationId(Long applicationId);
    Optional<SelectionEmailDelivery>
    findFirstByApplicationIdOrderByCreatedAtDesc(
            Long applicationId
    );
}