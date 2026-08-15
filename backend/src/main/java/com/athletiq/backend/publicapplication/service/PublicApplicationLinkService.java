package com.athletiq.backend.publicapplication.service;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.event.repository.EventRepository;
import com.athletiq.backend.form.entity.Form;
import com.athletiq.backend.form.entity.FormVersion;
import com.athletiq.backend.form.entity.FormVersionStatus;
import com.athletiq.backend.form.repository.FormRepository;
import com.athletiq.backend.form.repository.FormVersionRepository;
import com.athletiq.backend.publicapplication.dto.PublicApplicationLinkResponse;
import com.athletiq.backend.publicapplication.entity.PublicApplicationLink;
import com.athletiq.backend.publicapplication.repository.PublicApplicationLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class PublicApplicationLinkService {

    private static final String CODE_PREFIX = "EVT-";
    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final EventRepository eventRepository;
    private final FormRepository formRepository;
    private final FormVersionRepository formVersionRepository;
    private final PublicApplicationLinkRepository publicApplicationLinkRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public PublicApplicationLinkService(
            EventRepository eventRepository,
            FormRepository formRepository,
            FormVersionRepository formVersionRepository,
            PublicApplicationLinkRepository publicApplicationLinkRepository
    ) {
        this.eventRepository = eventRepository;
        this.formRepository = formRepository;
        this.formVersionRepository = formVersionRepository;
        this.publicApplicationLinkRepository =
                publicApplicationLinkRepository;
    }

    @Transactional
    public PublicApplicationLinkResponse generateOrGet(
            Long organizerId,
            Long eventId,
            String publicBaseUrl
    ) {
        Event event = eventRepository
                .findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event not found or access denied."
                        )
                );

        Form form = formRepository
                .findByEventId(eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application form has not been created."
                        )
                );

        Long publishedVersionId =
                form.getCurrentPublishedVersionId();

        if (publishedVersionId == null) {
            throw new IllegalStateException(
                    "A published application form is required before generating a public link."
            );
        }

        FormVersion formVersion =
                formVersionRepository.findById(publishedVersionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Current published form version was not found."
                                )
                        );

        if (formVersion.getStatus() != FormVersionStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Current form version is not published."
            );
        }

        PublicApplicationLink existing =
                publicApplicationLinkRepository
                        .findByEventId(eventId)
                        .orElse(null);

        if (existing != null) {
            if (!existing.getFormVersion().getId()
                    .equals(publishedVersionId)) {

                existing.setFormVersion(formVersion);
                existing.setActive(true);

                existing =
                        publicApplicationLinkRepository.save(existing);
            }

            return toResponse(existing, publicBaseUrl);
        }

        PublicApplicationLink link =
                new PublicApplicationLink();

        link.setEvent(event);
        link.setFormVersion(formVersion);
        link.setPublicCode(generateUniqueCode());
        link.setActive(true);

        link =
                publicApplicationLinkRepository.save(link);

        return toResponse(link, publicBaseUrl);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String code = CODE_PREFIX + randomPart(8);

            if (!publicApplicationLinkRepository
                    .existsByPublicCode(code)) {
                return code;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique public application code."
        );
    }

    private String randomPart(int length) {
        StringBuilder value =
                new StringBuilder(length);

        for (int index = 0; index < length; index++) {
            value.append(
                    ALPHABET.charAt(
                            secureRandom.nextInt(ALPHABET.length())
                    )
            );
        }

        return value.toString();
    }

    private PublicApplicationLinkResponse toResponse(
            PublicApplicationLink link,
            String publicBaseUrl
    ) {
        String base =
                publicBaseUrl == null ||
                publicBaseUrl.isBlank()
                        ? ""
                        : publicBaseUrl.replaceAll("/+$", "");

        String publicUrl =
                base + "/apply/" + link.getPublicCode();

        return new PublicApplicationLinkResponse(
                link.getId(),
                link.getEvent().getId(),
                link.getFormVersion().getId(),
                link.getFormVersion().getVersionNumber(),
                link.getPublicCode(),
                publicUrl,
                link.isActive()
        );
    }
}