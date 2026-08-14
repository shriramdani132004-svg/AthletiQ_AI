package com.athletiq.backend.form.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "form_fields",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form_field_key",
                        columnNames = {"form_version_id", "field_key"}
                ),
                @UniqueConstraint(
                        name = "uk_form_field_order",
                        columnNames = {"form_version_id", "display_order"}
                )
        }
)
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "form_version_id",
            nullable = false
    )
    private FormVersion formVersion;

    @Column(name = "field_key", nullable = false, length = 100)
    private String fieldKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 30)
    private FieldType fieldType;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "validation_config", columnDefinition = "TEXT")
    private String validationConfig;

    @Column(name = "options_config", columnDefinition = "TEXT")
    private String optionsConfig;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    public FormVersion getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(FormVersion formVersion) {
        this.formVersion = formVersion;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getValidationConfig() {
        return validationConfig;
    }

    public void setValidationConfig(String validationConfig) {
        this.validationConfig = validationConfig;
    }

    public String getOptionsConfig() {
        return optionsConfig;
    }

    public void setOptionsConfig(String optionsConfig) {
        this.optionsConfig = optionsConfig;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
