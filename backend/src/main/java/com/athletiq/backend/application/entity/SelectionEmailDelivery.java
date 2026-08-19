package com.athletiq.backend.application.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "selection_email_deliveries",
        indexes = {
                @Index(
                        name = "idx_selection_email_application",
                        columnList = "application_id"
                )
        }
)
public class SelectionEmailDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "application_id",
            nullable = false
    )
    private Long applicationId;

    @Column(
            name = "recipient_email",
            nullable = false,
            length = 320
    )
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private EmailDeliveryStatus status;

    @Column(
            name = "failure_message",
            columnDefinition = "TEXT"
    )
    private String failureMessage;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    protected SelectionEmailDelivery() {
    }

    public SelectionEmailDelivery(
            Long applicationId,
            String recipientEmail
    ) {
        this.applicationId =
                applicationId;

        this.recipientEmail =
                recipientEmail;

        this.status =
                EmailDeliveryStatus.QUEUED;

        this.createdAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public EmailDeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(
            EmailDeliveryStatus status
    ) {
        this.status = status;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(
            String failureMessage
    ) {
        this.failureMessage =
                failureMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(
            LocalDateTime sentAt
    ) {
        this.sentAt = sentAt;
    }
}