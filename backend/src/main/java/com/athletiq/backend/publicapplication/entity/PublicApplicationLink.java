package com.athletiq.backend.publicapplication.entity;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.form.entity.FormVersion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "public_application_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_public_application_event",
                        columnNames = "event_id"
                ),
                @UniqueConstraint(
                        name = "uk_public_application_code",
                        columnNames = "public_code"
                )
        }
)
public class PublicApplicationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "form_version_id",
            nullable = false
    )
    private FormVersion formVersion;

    @Column(
            name = "public_code",
            nullable = false,
            unique = true,
            length = 32
    )
    private String publicCode;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public FormVersion getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(FormVersion formVersion) {
        this.formVersion = formVersion;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String publicCode) {
        this.publicCode = publicCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}