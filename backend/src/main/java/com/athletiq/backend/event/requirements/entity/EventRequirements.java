package com.athletiq.backend.event.requirements.entity;

import com.athletiq.backend.event.entity.Event;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "event_requirements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_requirements_event",
                        columnNames = "event_id"
                )
        }
)
public class EventRequirements {

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

    @Column(name = "required_positions", columnDefinition = "TEXT")
    private String requiredPositions;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "minimum_experience", columnDefinition = "TEXT")
    private String minimumExperience;

    @Column(name = "required_achievements", columnDefinition = "TEXT")
    private String requiredAchievements;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "performance_requirements", columnDefinition = "TEXT")
    private String performanceRequirements;

    @Column(name = "fitness_requirements", columnDefinition = "TEXT")
    private String fitnessRequirements;

    @Column(name = "availability_requirements", columnDefinition = "TEXT")
    private String availabilityRequirements;

    @Column(name = "eligibility_conditions", columnDefinition = "TEXT")
    private String eligibilityConditions;

    @Column(name = "event_specific_requirements", columnDefinition = "TEXT")
    private String eventSpecificRequirements;

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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getRequiredPositions() {
        return requiredPositions;
    }

    public void setRequiredPositions(String requiredPositions) {
        this.requiredPositions = requiredPositions;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getMinimumExperience() {
        return minimumExperience;
    }

    public void setMinimumExperience(String minimumExperience) {
        this.minimumExperience = minimumExperience;
    }

    public String getRequiredAchievements() {
        return requiredAchievements;
    }

    public void setRequiredAchievements(String requiredAchievements) {
        this.requiredAchievements = requiredAchievements;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getPerformanceRequirements() {
        return performanceRequirements;
    }

    public void setPerformanceRequirements(String performanceRequirements) {
        this.performanceRequirements = performanceRequirements;
    }

    public String getFitnessRequirements() {
        return fitnessRequirements;
    }

    public void setFitnessRequirements(String fitnessRequirements) {
        this.fitnessRequirements = fitnessRequirements;
    }

    public String getAvailabilityRequirements() {
        return availabilityRequirements;
    }

    public void setAvailabilityRequirements(String availabilityRequirements) {
        this.availabilityRequirements = availabilityRequirements;
    }

    public String getEligibilityConditions() {
        return eligibilityConditions;
    }

    public void setEligibilityConditions(String eligibilityConditions) {
        this.eligibilityConditions = eligibilityConditions;
    }

    public String getEventSpecificRequirements() {
        return eventSpecificRequirements;
    }

    public void setEventSpecificRequirements(String eventSpecificRequirements) {
        this.eventSpecificRequirements = eventSpecificRequirements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}