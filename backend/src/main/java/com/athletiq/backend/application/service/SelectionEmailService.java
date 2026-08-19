package com.athletiq.backend.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.EmailDeliveryStatus;
import com.athletiq.backend.application.entity.SelectionEmailDelivery;
import com.athletiq.backend.application.repository.SelectionEmailDeliveryRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SelectionEmailService {

    private final JavaMailSender mailSender;
    private final SelectionEmailDeliveryRepository deliveryRepository;
    private final boolean enabled;
    private final String from;
    private final PlayerResponseTokenService tokenService;
    private final String publicBaseUrl;
    public SelectionEmailService(
            JavaMailSender mailSender,
            SelectionEmailDeliveryRepository deliveryRepository,
            @Value("${athletiq.mail.enabled:false}")
            boolean enabled,
            @Value("${athletiq.mail.from:no-reply@athletiq.local}")
            String from,
            PlayerResponseTokenService tokenService,
@Value("${athletiq.public-base-url:http://localhost:5173}")
String publicBaseUrl
    ) {
        this.mailSender = mailSender;
        this.deliveryRepository =
                deliveryRepository;
        this.enabled = enabled;
        this.from = from;
        this.tokenService =
        tokenService;

this.publicBaseUrl =
        publicBaseUrl;
    }

    @Transactional
    public void sendSelectionEmail(
            Application application
    ) {
        sendProfessionalEmail(application);
    }

    @Transactional
    public void sendCustomSelectionEmail(
            Application application,
            String ignoredSubject,
            String ignoredMessage
    ) {
        sendProfessionalEmail(application);
    }

    private void sendProfessionalEmail(
            Application application
    ) {
        if (!enabled) {
            throw new IllegalStateException(
                    "Email delivery is disabled."
            );
        }

        if (application.getApplicantEmail() == null ||
                application.getApplicantEmail().isBlank()) {
            throw new IllegalStateException(
                    "Applicant email is missing."
            );
        }

        SelectionEmailDelivery delivery =
                new SelectionEmailDelivery(
                        application.getId(),
                        application.getApplicantEmail()
                );

        delivery =
                deliveryRepository.save(
                        delivery
                );

        String applicantName =
                safe(
                        application.getApplicantName(),
                        "Applicant"
                );
        String responseToken =
        tokenService.createToken(
                application
        );

String acceptUrl =
        publicBaseUrl +
        "/player-response/" +
        responseToken +
        "/accept";

String declineUrl =
        publicBaseUrl +
        "/player-response/" +
        responseToken +
        "/decline";

        String eventName =
                application.getEvent() == null
                        ? "AthletiQ event"
                        : safe(
                                application.getEvent().getName(),
                                "AthletiQ event"
                        );

        try {
            MimeMessage mimeMessage =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(from);
            helper.setTo(
                    application.getApplicantEmail()
            );
            helper.setSubject(
                    "Congratulations — You have been selected for " +
                    eventName
            );
            helper.setText(
                    createHtml(
        applicantName,
        eventName,
        acceptUrl,
        declineUrl
),
                    true
            );

            mailSender.send(mimeMessage);

            delivery.setStatus(
                    EmailDeliveryStatus.SENT
            );
            delivery.setSentAt(
                    java.time.LocalDateTime.now()
            );

            deliveryRepository.save(
                    delivery
            );

        } catch (MessagingException |
                 RuntimeException exception) {

            delivery.setStatus(
                    EmailDeliveryStatus.FAILED
            );
            delivery.setFailureMessage(
                    exception.getMessage()
            );

            deliveryRepository.save(
                    delivery
            );

            throw new IllegalStateException(
                    "Unable to send selection email.",
                    exception
            );
        }
    }

    private String createHtml(
        String applicantName,
        String eventName,
        String acceptUrl,
        String declineUrl
) {
        return """
                <html>
                <body style="margin:0;background:#eef3f8;font-family:Arial,sans-serif;color:#172033;">
                    <div style="max-width:620px;margin:32px auto;background:#ffffff;border-radius:20px;overflow:hidden;">
                        <div style="padding:32px;background:linear-gradient(135deg,#101a30,#273b86);color:#ffffff;">
                            <div style="font-size:28px;font-weight:800;">AthletiQ</div>
                            <div style="margin-top:10px;color:#9feaff;font-size:13px;font-weight:700;letter-spacing:.12em;">
                                PLAYER SELECTION
                            </div>
                        </div>

                        <div style="padding:36px 32px;">
                            <h1 style="color:#172033;">
                                Congratulations, %s!
                            </h1>

                            <p style="font-size:16px;line-height:1.7;">
                                You have been selected for:
                            </p>

                            <div style="padding:20px;border-radius:14px;background:#eef2ff;color:#293582;font-size:21px;font-weight:800;">
                                %s
                            </div>

                            <p style="font-size:16px;line-height:1.7;">
                                The organizer has reviewed your application and selected you for the next stage.
                            </p>
                        </div>

                        <div style="padding:22px 32px;background:#f8fafc;color:#64748b;font-size:13px;">
                            This message was sent by AthletiQ on behalf of the event organizer.
                        </div>
                        <div style="margin-top:30px;text-align:center;">
    <a href="%s"
       style="display:inline-block;padding:14px 24px;margin:6px;border-radius:10px;background:#16a34a;color:#ffffff;text-decoration:none;font-weight:800;">
        ACCEPT PARTICIPATION
    </a>

    <a href="%s"
       style="display:inline-block;padding:14px 24px;margin:6px;border-radius:10px;background:#e11d48;color:#ffffff;text-decoration:none;font-weight:800;">
        DECLINE
    </a>
</div>
                    </div>
                </body>
                </html>
                """.formatted(
        escape(applicantName),
        escape(eventName),
        acceptUrl,
        declineUrl
);
    }

    private String safe(
            String value,
            String fallback
    ) {
        return value == null ||
                value.isBlank()
                ? fallback
                : value.trim();
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}