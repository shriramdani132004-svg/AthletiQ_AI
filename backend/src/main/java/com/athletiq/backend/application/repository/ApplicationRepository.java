package com.athletiq.backend.application.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.ApplicationStatus;
import com.athletiq.backend.application.entity.SelectionStatus;

public interface ApplicationRepository
        extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    // --------------------------------------------------------
    // EXISTING CONTRACT - DO NOT REMOVE
    // --------------------------------------------------------

    List<Application> findByEventId(Long eventId);

    List<Application> findByFormVersionId(Long formVersionId);

    List<Application> findByApplicantId(Long applicantId);

    // --------------------------------------------------------
    // PAGINATED APPLICATION QUERIES
    // --------------------------------------------------------

    Page<Application> findByEventId(
            Long eventId,
            Pageable pageable
    );

    Page<Application>
    findByEventIdAndApplicantNameContainingIgnoreCase(
            Long eventId,
            String applicantName,
            Pageable pageable
    );

    Page<Application>
    findByEventIdAndApplicantEmailContainingIgnoreCase(
            Long eventId,
            String applicantEmail,
            Pageable pageable
    );

    Page<Application> findByEventIdAndStatus(
            Long eventId,
            ApplicationStatus status,
            Pageable pageable
    );

    @Query("""
            select a
            from Application a
            where a.event.id = :eventId
              and (
                    lower(coalesce(a.applicantName, ''))
                        like lower(concat('%', :search, '%'))
                 or lower(coalesce(a.applicantEmail, ''))
                        like lower(concat('%', :search, '%'))
                 or lower(coalesce(a.applicantPhone, ''))
                        like lower(concat('%', :search, '%'))
              )
            """)
    Page<Application> searchEventApplications(
            @Param("eventId") Long eventId,
            @Param("search") String search,
            Pageable pageable
    );

    // --------------------------------------------------------
    // STATISTICS
    // --------------------------------------------------------

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(
            Long eventId,
            ApplicationStatus status
    );
    long countByEventIdAndSelectionStatusNot(
        Long eventId,
        SelectionStatus selectionStatus
);

long countByEventIdAndSelectionStatus(
        Long eventId,
        SelectionStatus selectionStatus
);

    // --------------------------------------------------------
    // DUPLICATE PROTECTION - EXISTING CONTRACT
    // --------------------------------------------------------

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
              and lower(a.applicantEmail) = lower(:email)
            """)
    boolean existsByEventAndEmail(
            @Param("eventId") Long eventId,
            @Param("email") String email
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
}