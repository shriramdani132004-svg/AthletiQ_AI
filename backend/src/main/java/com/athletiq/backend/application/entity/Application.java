package com.athletiq.backend.application.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

import com.athletiq.backend.event.entity.Event;
import com.athletiq.backend.form.entity.FormVersion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
@Entity
@Table(
        name = "applications",
        indexes = {
                @Index(
                        name = "idx_application_event",
                        columnList = "event_id"
                ),
                @Index(
                        name = "idx_application_form_version",
                        columnList = "form_version_id"
                ),
                @Index(
                        name = "idx_application_applicant",
                        columnList = "applicant_id"
                ),
                @Index(
                        name = "idx_application_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_application_email",
                        columnList = "applicant_email"
                ),
                @Index(
                        name = "idx_application_phone",
                        columnList = "applicant_phone"
                )
        }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Formula("""
    (
        select ace.ai_score
        from ai_candidate_evaluations ace
        where ace.application_id = id
        order by ace.evaluated_at desc
        limit 1
    )
""")
private Double aiScore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "form_version_id",
            nullable = false
    )
    private FormVersion formVersion;

    /**
     * Existing authenticated applicant reference.
     *
     * Public applications may legitimately have no authenticated
     * applicant identity, therefore this field is nullable.
     */
    @Column(name = "applicant_id")
    private Long applicantId;

    /**
     * Standard player identity fields.
     *
     * These are deliberately nullable because Phase 8 validation
     * will enforce requirements based on the published form.
     */
    @Column(
            name = "applicant_name",
            length = 255
    )
    private String applicantName;

    @Column(
            name = "applicant_email",
            length = 320
    )
    private String applicantEmail;

    @Column(
            name = "applicant_phone",
            length = 40
    )
    private String applicantPhone;

    /**
     * Dynamic answers exactly as submitted against the published form.
     */
    @Column(
            name = "submitted_data",
            columnDefinition = "TEXT",
            nullable = false
    )
    private String submittedData;

    /**
     * Foundation for future file uploads.
     *
     * Actual file storage will be implemented in Phase 8 Step 5.
     * This field stores metadata only.
     */
    @Column(
            name = "file_metadata",
            columnDefinition = "TEXT"
    )
    private String fileMetadata;

        @Column(
            name = "duplicate_email",
            length = 320
    )
    private String duplicateEmail;

    @Column(
            name = "duplicate_phone",
            length = 40
    )
    private String duplicatePhone;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 40
    )
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
@Column(
        name = "selection_status",
        nullable = false,
        length = 30
)
private SelectionStatus selectionStatus;

@Column(
        name = "selection_reason",
        columnDefinition = "TEXT"
)
private String selectionReason;

@Column(
        name = "selection_decided_at"
)
private LocalDateTime selectionDecidedAt;

    @Column(
            name = "submitted_at",
            nullable = false
    )
    private LocalDateTime submittedAt;

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

    public Application() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if (submittedAt == null) {
            submittedAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = ApplicationStatus.SUBMITTED;
        }
        if (selectionStatus == null) {
    selectionStatus = SelectionStatus.NOT_REVIEWED;
}

        if (submittedData == null) {
            submittedData = "{}";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Double getAiScore() {
    return aiScore;
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

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getSubmittedData() {
        return submittedData;
    }

    public void setSubmittedData(String submittedData) {
        this.submittedData = submittedData;
    }

    public String getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(String fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public String getDuplicateEmail() {
        return duplicateEmail;
    }

    public void setDuplicateEmail(String duplicateEmail) {
        this.duplicateEmail = duplicateEmail;
    }

    public String getDuplicatePhone() {
        return duplicatePhone;
    }

    public void setDuplicatePhone(String duplicatePhone) {
        this.duplicatePhone = duplicatePhone;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public SelectionStatus getSelectionStatus() {
    return selectionStatus;
}

public void setSelectionStatus(
        SelectionStatus selectionStatus
) {
    this.selectionStatus = selectionStatus;
}

public String getSelectionReason() {
    return selectionReason;
}

public void setSelectionReason(
        String selectionReason
) {
    this.selectionReason = selectionReason;
}

public LocalDateTime getSelectionDecidedAt() {
    return selectionDecidedAt;
}

public void setSelectionDecidedAt(
        LocalDateTime selectionDecidedAt
) {
    this.selectionDecidedAt = selectionDecidedAt;
}
}