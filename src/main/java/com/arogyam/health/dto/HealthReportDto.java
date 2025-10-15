package com.arogyam.health.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class HealthReportDto {

    @NotBlank(message = "Patient name is required")
    @Size(max = 200, message = "Patient name must not exceed 200 characters")
    private String patientName;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age cannot exceed 150")
    private Integer patientAge;

    @NotBlank(message = "Patient gender is required")
    private String patientGender;  // "MALE", "FEMALE", "OTHER"

    @NotEmpty(message = "At least one symptom is required")
    private List<String> symptoms;

    @NotBlank(message = "Severity level is required")
    private String severityLevel;  // "MILD", "MODERATE", "SEVERE"

    @Size(max = 100, message = "Suspected disease must not exceed 100 characters")
    private String suspectedDisease;

    @NotNull(message = "Village ID is required")
    private Long villageId;

    private LocalDate reportDate;

    private LocalTime reportTime;

    @Size(max = 5000, message = "Additional notes must not exceed 5000 characters")
    private String additionalNotes;

    // Coordinates (optional)
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    // Constructors
    public HealthReportDto() {}

    // Getters and Setters
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

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getSuspectedDisease() {
        return suspectedDisease;
    }

    public void setSuspectedDisease(String suspectedDisease) {
        this.suspectedDisease = suspectedDisease;
    }

    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "HealthReportDto{" +
                "patientName='" + patientName + '\'' +
                ", patientAge=" + patientAge +
                ", patientGender='" + patientGender + '\'' +
                ", symptoms=" + symptoms +
                ", severityLevel='" + severityLevel + '\'' +
                ", suspectedDisease='" + suspectedDisease + '\'' +
                ", villageId=" + villageId +
                ", reportDate=" + reportDate +
                ", reportTime=" + reportTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}