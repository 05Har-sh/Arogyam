package com.arogyam.health.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
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
@Table(name = "health_reports")
@EntityListeners(AuditingEntityListener.class)
public class HealthReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String patientName;

    @Column
    private Integer patientAge;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender patientGender;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false) // Use columnDefinition
    private List<String> symptoms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeverityLevel severityLevel;

    @Column(length = 100)
    private String suspectedDisease;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false)
    private LocalTime reportTime;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private VillageEntity village;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point locationCoordinates;

    @Column(nullable = false)
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

    public Point getLocationCoordinates() { return locationCoordinates; }
    public void setLocationCoordinates(Point locationCoordinates) { this.locationCoordinates = locationCoordinates; }

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Integer getPatientAge() { return patientAge; }
    public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }

    public Gender getPatientGender() { return patientGender; }
    public void setPatientGender(Gender patientGender) { this.patientGender = patientGender; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }

    public String getSuspectedDisease() { return suspectedDisease; }
    public void setSuspectedDisease(String suspectedDisease) { this.suspectedDisease = suspectedDisease; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public LocalTime getReportTime() { return reportTime; }
    public void setReportTime(LocalTime reportTime) { this.reportTime = reportTime; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

    public UserEntity getReporter() { return reporter; }
    public void setReporter(UserEntity reporter) { this.reporter = reporter; }

    public VillageEntity getVillage() { return village; }
    public void setVillage(VillageEntity village) { this.village = village; }

    // Utility getters that extract coordinates from the Point object
    public Double getLatitude() {
        return this.locationCoordinates != null ? this.locationCoordinates.getY() : null;
    }
    public Double getLongitude() {
        return this.locationCoordinates != null ? this.locationCoordinates.getX() : null;
    }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public UserEntity getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(UserEntity verifiedBy) { this.verifiedBy = verifiedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum SeverityLevel {
        MILD, MODERATE, SEVERE
    }


    public enum Gender {
        MALE, FEMALE, OTHER
    }

}
