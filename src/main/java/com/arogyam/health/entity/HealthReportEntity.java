package com.arogyam.health.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "health_reports", indexes = {
        @Index(name = "idx_reporter_id", columnList = "reporter_id"),
        @Index(name = "idx_village_id", columnList = "village_id"),
        @Index(name = "idx_report_date", columnList = "reportDate"),
        @Index(name = "idx_severity_level", columnList = "severityLevel"),
        @Index(name = "idx_is_verified", columnList = "isVerified"),
        @Index(name = "idx_suspected_disease", columnList = "suspectedDisease"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class HealthReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 200, message = "Patient name must be between 2 and 200 characters")
    private String patientName;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 120, message = "Age cannot exceed 120 years")
    @Column
    private Integer patientAge;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender patientGender;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    @NotEmpty(message = "Atleast one Symptom is required")
    private List<String> symptoms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Severity level is required")
    private SeverityLevel severityLevel;

    @Size(max = 100, message = "Suspected disease cannot exceed 100 characters")
    @Column(length = 100)
    private String suspectedDisease;

    @Column(nullable = false)
    @NotNull(message = "Report date is required")
    @PastOrPresent(message = "Report date cannot be in the future")
    private LocalDate reportDate;

    @Column(nullable = false)
    @NotNull(message = "Report time is required")
    private LocalTime reportTime;

    @Size(max = 5000, message = "Additional notes cannot exceed 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @NotNull(message = "Reporter is required")
    private UserEntity reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    @NotNull(message = "Village is required")
    private VillageEntity village;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point locationCoordinates;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private UserEntity verifiedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public HealthReportEntity() {}

    public HealthReportEntity(String patientName, List<String> symptoms,
                              SeverityLevel severityLevel, UserEntity reporter,
                              VillageEntity village, LocalDate reportDate,
                              LocalTime reportTime) {
        this.patientName = patientName;
        this.symptoms = symptoms;
        this.severityLevel = severityLevel;
        this.reporter = reporter;
        this.village = village;
        this.reportDate = reportDate;
        this.reportTime = reportTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(Integer patientAge) {
        this.patientAge = patientAge;
    }

    public Gender getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(Gender patientGender) {
        this.patientGender = patientGender;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getSuspectedDisease() {
        return suspectedDisease;
    }

    public void setSuspectedDisease(String suspectedDisease) {
        this.suspectedDisease = suspectedDisease;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public LocalTime getReportTime() {
        return reportTime;
    }

    public void setReportTime(LocalTime reportTime) {
        this.reportTime = reportTime;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public UserEntity getReporter() {
        return reporter;
    }

    public void setReporter(UserEntity reporter) {
        this.reporter = reporter;
    }

    public VillageEntity getVillage() {
        return village;
    }

    public void setVillage(VillageEntity village) {
        this.village = village;
    }

    public Point getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(Point locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }
    // Utility getters that extract coordinates from the Point object
    public Double getLatitude() {
        return this.locationCoordinates != null ? this.locationCoordinates.getY() : null;
    }

    public Double getLongitude() {
        return this.locationCoordinates != null ? this.locationCoordinates.getX() : null;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public UserEntity getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(UserEntity verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSevere() {
        return this.severityLevel == SeverityLevel.SEVERE;
    }

    public boolean needsImmediateAttention() {
        return this.severityLevel == SeverityLevel.SEVERE && !this.isVerified;
    }

    public boolean hasLocation() {
        return this.locationCoordinates != null;
    }

    public String getFormattedDateTime() {
        return this.reportDate.toString() + " " + this.reportTime.toString();
    }
    public enum SeverityLevel {
        MILD,
        MODERATE,
        SEVERE
    }


    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

}
