package com.arogyam.health.dto;

import jakarta.validation.constraints.*;

public class WaterQualityDto {

    @NotBlank(message = "Source name is required")
    @Size(max = 200, message = "Source name must not exceed 200 characters")
    private String sourceName;

    @NotBlank(message = "Source type is required")
    private String sourceType; // "WELL", "TUBE_WELL", "POND", "RIVER", "LAKE", "TAP_WATER", "OTHER"

    @DecimalMin(value = "0.0", message = "pH level must be at least 0")
    @DecimalMax(value = "14.0", message = "pH level cannot exceed 14")
    private Double phLevel;

    @DecimalMin(value = "0.0", message = "Turbidity must be non-negative")
    private Double turbidity;

    @Min(value = 0, message = "Bacterial count must be non-negative")
    private Integer bacterialCount;

    @DecimalMin(value = "-10.0", message = "Temperature must be at least -10°C")
    @DecimalMax(value = "100.0", message = "Temperature cannot exceed 100°C")
    private Double temperature;

    @NotBlank(message = "Quality status is required")
    private String qualityStatus; // "SAFE", "MODERATE_RISK", "HIGH_RISK", "CONTAMINATED"

    @NotNull(message = "Village ID is required")
    private Long villageId;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    // Constructors
    public WaterQualityDto() {}

    // Getters and Setters
    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Double getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(Double phLevel) {
        this.phLevel = phLevel;
    }

    public Double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(Double turbidity) {
        this.turbidity = turbidity;
    }

    public Integer getBacterialCount() {
        return bacterialCount;
    }

    public void setBacterialCount(Integer bacterialCount) {
        this.bacterialCount = bacterialCount;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(String qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "WaterQualityDto{" +
                "sourceName='" + sourceName + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", phLevel=" + phLevel +
                ", turbidity=" + turbidity +
                ", bacterialCount=" + bacterialCount +
                ", temperature=" + temperature +
                ", qualityStatus='" + qualityStatus + '\'' +
                ", villageId=" + villageId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}