package com.athletiq.backend.application.service;

import com.athletiq.backend.application.dto.OrganizerApplicationDetailResponse;

import com.athletiq.backend.event.entity.Event;

import com.athletiq.backend.event.repository.EventRepository;

import org.springframework.data.jpa.domain.Specification;

import com.athletiq.backend.application.specification.ApplicationSpecification;



import com.athletiq.backend.application.dto.OrganizerApplicationPageResponse;
import com.athletiq.backend.application.dto.OrganizerApplicationStatisticsResponse;
import com.athletiq.backend.application.dto.OrganizerApplicationSummaryResponse;
import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.ApplicationStatus;
import com.athletiq.backend.application.repository.ApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrganizerApplicationService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final ApplicationRepository applicationRepository;

    private final EventRepository eventRepository;

    public OrganizerApplicationService(
            ApplicationRepository applicationRepository,
            EventRepository eventRepository
    ) {
        this.applicationRepository =
                applicationRepository;

        this.eventRepository =
                eventRepository;
    }

    @Transactional
    public OrganizerApplicationPageResponse getApplications(
            Long organizerId,
            Long eventId,
            int page,
            int size,
            String search,
            String email,
            Integer age,
            String position,
            ApplicationStatus status,
            String sort,
            String direction
    ) {

        if(organizerId == null){
            throw new IllegalArgumentException(
                    "Organizer ID is required."
            );
        }

        if(eventId == null){
            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        if(page < 0){
            throw new IllegalArgumentException(
                    "Page must be zero or greater."
            );
        }

        Event ownedEvent =
                eventRepository
                        .findByIdAndOrganizerId(
                                eventId,
                                organizerId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Event not found or access denied."
                                )
                        );
        int normalizedSize =
                size <= 0
                        ? DEFAULT_PAGE_SIZE
                        : Math.min(size, MAX_PAGE_SIZE);

        Sort sortOrder =
                resolveSort(
                        sort,
                        direction
                );

        Pageable pageable =
                PageRequest.of(
                        page,
                        normalizedSize,
                        sortOrder
                );

        String normalizedSearch =
                normalize(search);

        String normalizedEmail =
                normalize(email);

        String normalizedPosition =
                normalize(position);

        Specification<Application> specification =
                ApplicationSpecification.eventId(
                        eventId
                );

        if(normalizedSearch != null){

            specification =
                    specification.and(
                            ApplicationSpecification.search(
                                    normalizedSearch
                            )
                    );
        }

        if(normalizedEmail != null){

            specification =
                    specification.and(
                            ApplicationSpecification.email(
                                    normalizedEmail
                            )
                    );
        }

        if(normalizedPosition != null){

            specification =
                    specification.and(
                            ApplicationSpecification.position(
                                    normalizedPosition
                            )
                    );
        }

        if(age != null){

            specification =
                    specification.and(
                            ApplicationSpecification.age(
                                    age
                            )
                    );
        }

        if(status != null){

            specification =
                    specification.and(
                            ApplicationSpecification.status(
                                    status
                            )
                    );
        }

        Page<Application> result =
                applicationRepository.findAll(
                        specification,
                        pageable
                );
        List<OrganizerApplicationSummaryResponse> content =
                result.getContent()
                        .stream()
                        .map(this::toSummary)
                        .toList();

        return new OrganizerApplicationPageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

@Transactional(readOnly = true)
public OrganizerApplicationStatisticsResponse getStatistics(
        Long organizerId,
        Long eventId
){

    if(organizerId == null){

        throw new IllegalArgumentException(
                "Organizer ID is required."
        );
    }

    if(eventId == null){

        throw new IllegalArgumentException(
                "Event ID is required."
        );
    }

    eventRepository
            .findByIdAndOrganizerId(
                    eventId,
                    organizerId
            )
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Event not found or access denied."
                    )
            );

    long total =
            applicationRepository
                    .countByEventId(eventId);

    long pending =
            applicationRepository
                    .countByEventIdAndStatus(
                            eventId,
                            ApplicationStatus.EVALUATION_PENDING
                    );

    /*
     * Current ApplicationStatus contains only:
     *
     * SUBMITTED
     * VALIDATED
     * EVALUATION_PENDING
     *
     * There is currently no persisted evaluation-result,
     * selection, acceptance, or decline model.
     *
     * Therefore these four dashboard metrics intentionally
     * remain zero until the evaluation/selection domain is
     * implemented. We do not derive fake business states from
     * unrelated status values.
     */

    long evaluated = 0;
    long selected = 0;
    long accepted = 0;
    long declined = 0;

    return new OrganizerApplicationStatisticsResponse(
            total,
            pending,
            evaluated,
            selected,
            accepted,
            declined
    );
}


    @Transactional(readOnly = true)
    public OrganizerApplicationDetailResponse getApplicationDetail(
            Long organizerId,
            Long eventId,
            Long applicationId
    ){

        if(organizerId == null){

            throw new IllegalArgumentException(
                    "Organizer ID is required."
            );
        }

        if(eventId == null){

            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        if(applicationId == null){

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        eventRepository
                .findByIdAndOrganizerId(
                        eventId,
                        organizerId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found or access denied."
                        )
                );

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Application not found."
                                )
                        );

        if(
                application.getEvent() == null ||
                application.getEvent().getId() == null ||
                !eventId.equals(
                        application.getEvent().getId()
                )
        ){

            throw new IllegalArgumentException(
                    "Application does not belong to this event."
            );
        }

        Integer formVersionNumber = null;

        if(application.getFormVersion() != null){

            formVersionNumber =
                    application
                            .getFormVersion()
                            .getVersionNumber();
        }

        return new OrganizerApplicationDetailResponse(
                application.getId(),
                application.getEvent().getId(),
                application.getFormVersion() == null
                        ? null
                        : application.getFormVersion().getId(),
                formVersionNumber,
                application.getApplicantName(),
                application.getApplicantEmail(),
                application.getApplicantPhone(),
                extractAge(application),
                extractPosition(application),
                null,
                null,
                null,
                null,
                application.getSubmittedData(),
                application.getFileMetadata(),
                extractScore(application),
                null,
                application.getStatus(),
                application.getSubmittedAt(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
    private OrganizerApplicationSummaryResponse toSummary(
            Application application
    ){

        return new OrganizerApplicationSummaryResponse(
                application.getId(),
                application.getEvent().getId(),
                application.getApplicantName(),
                application.getApplicantEmail(),
                application.getApplicantPhone(),
                extractAge(application),
                extractPosition(application),
                extractScore(application),
                null,
                application.getStatus(),
                application.getSubmittedAt(),
                application.getFormVersion().getId()
        );
    }

    private Integer extractAge(
            Application application
    ){
        return null;
    }

    private String extractPosition(
            Application application
    ){
        return null;
    }

    private BigDecimal extractScore(
            Application application
    ){
        return null;
    }

    private Sort resolveSort(
            String sort,
            String direction
    ){

        String property;

        if(sort == null || sort.isBlank()){
            property = "submittedAt";
        }else{

            property =
                    switch(sort.trim().toLowerCase()){
                        case "date",
                             "applicationdate",
                             "submittedat" ->
                                "submittedAt";

                        case "name",
                             "player" ->
                                "applicantName";

                        case "email" ->
                                "applicantEmail";

                        case "phone" ->
                                "applicantPhone";

                        case "status" ->
                                "status";

                        default ->
                                throw new IllegalArgumentException(
                                        "Invalid application sort field: " +
                                                sort
                                );
                    };
        }

        Sort.Direction sortDirection =
                "asc".equalsIgnoreCase(direction)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(
                sortDirection,
                property
        );
    }

    private String normalize(
            String value
    ){

        if(value == null){
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }
}