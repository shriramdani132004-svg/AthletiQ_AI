package com.athletiq.backend.application.repository;

import com.athletiq.backend.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByEventId(Long eventId);

    List<Application> findByFormVersionId(Long formVersionId);

    List<Application> findByApplicantId(Long applicantId);

    boolean existsByEventIdAndApplicantId(
            Long eventId,
            Long applicantId
    );

    boolean existsByEventIdAndApplicantEmailIgnoreCase(
            Long eventId,
            String applicantEmail
    );

    boolean existsByEventIdAndApplicantPhone(
            Long eventId,
            String applicantPhone
    );

    @Query("""
            select count(a) > 0
            from Application a
            where a.event.id = :eventId
              and a.applicantPhone = :phone
            """)
    boolean existsByEventAndPhone(
            @Param("eventId") Long eventId,
            @Param("phone") String phone
    );

    @Query("""
            select count(a) > 0
            from Application a
            where a.event.id = :eventId
              and lower(a.applicantEmail) = lower(:email)
            """)
    boolean existsByEventAndEmail(
            @Param("eventId") Long eventId,
            @Param("email") String email
    );
}