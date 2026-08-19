package com.athletiq.backend.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.athletiq.backend.application.entity.Application;

@Service
public class SelectionEmailService {

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public SelectionEmailService(
            JavaMailSender mailSender,
            @Value("${athletiq.mail.enabled:false}")
            boolean enabled,
            @Value("${athletiq.mail.from:no-reply@athletiq.local}")
            String from
    ) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    public void sendSelectionEmail(
            Application application
    ) {
        if (!enabled) {
            return;
        }

        if (application.getApplicantEmail() == null ||
                application.getApplicantEmail().isBlank()) {
            return;
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(
                application.getApplicantEmail()
        );
        message.setSubject(
                "AthletiQ selection update"
        );
        message.setText(
                "Hello " +
                safeName(application.getApplicantName()) +
                ",\n\n" +
                "Congratulations. You have been selected " +
                "for the event \"" +
                application.getEvent().getName() +
                "\".\n\n" +
                "The organizer will contact you with the next steps.\n\n" +
                "Regards,\nAthletiQ"
        );

        mailSender.send(message);
    }

    private String safeName(String name) {
        return name == null ||
                name.isBlank()
                ? "Applicant"
                : name.trim();
    }
}