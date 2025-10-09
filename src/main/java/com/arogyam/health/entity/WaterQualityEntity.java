package com.arogyam.health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_quality_reports", indexes = {
        @Index(name = "idx_village_id", columnList = "village_id"),
        @Index(name = "idx_tester_id", columnList = "tester_id"),
        @Index(name = "idx_quality_status", columnList = "qualityStatus"),
        @Index(name = "idx_test_date", columnList = "testDate"),
        @Index(name = "idx_source_type", columnList = "sourceType")
})
@EntityListeners(AuditingEntityListener.class)
public class WaterQualityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Source name is required")
    @Size(max = 200, message = "Source name cannot exceed 200 characters")
    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length =20)
    @NotNull(message = "Source type is required")
    private SourceType sourceType;

    @DecimalMin(value = "0.0", message = "pH level must be atleast 0")
    @DecimalMax(value = "14.4", message = "pH level cannot exceed 14")
    @Column(precision = 4, scale = 2)
    private Double phLevel;

    @DecimalMin(value = "0.0", message = "Turbidity must be non-negative")
    @Column(precision = 4, scale = 2)
    private Double turbidity;

    @Min(value = 0, message = "Bacterial count must be non-negative")
    private Integer bacterialCount;

    @DecimalMin(value = "-10.0", message = "Temperature must be at least -10°C")
    @DecimalMax(value = "100.0", message = "Temperature cannot exceed 100°C")
    @Column(precision = 5, scale = 2)
    private Double temperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Quality status is required")
    private QualityStatus qualityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id", nullable = false)
    @NotNull(message = "Tester is required")
    private UserEntity tester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    @NotNull(message = "Village is required")
    private VillageEntity village;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(precision = 10, scale = 8)
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(precision = 11, scale = 8)
    private Double longitude;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    @Column(length = 500)
    private String remarks;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime testDate;

    // Constructors
    public WaterQualityEntity() {}

    public WaterQualityEntity(String sourceName, SourceType sourceType, QualityStatus qualityStatus,
                              UserEntity tester, VillageEntity village) {
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.qualityStatus = qualityStatus;
        this.tester = tester;
        this.village = village;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
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

    public QualityStatus getQualityStatus() {
        return qualityStatus;
    }

    public void setQualityStatus(QualityStatus qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public UserEntity getTester() {
        return tester;
    }

    public void setTester(UserEntity tester) {
        this.tester = tester;
    }

    public VillageEntity getVillage() {
        return village;
    }

    public void setVillage(VillageEntity village) {
        this.village = village;
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

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
    }

    public boolean isSafe(){
        return this.qualityStatus == QualityStatus.SAFE;
    }
    public boolean hasGeoLocation(){
        return this.latitude != null && this.longitude != null;
    }

    public enum SourceType {
        WELL,
        TUBE_WELL,
        POND,
        RIVER,
        LAKE,
        TAP_WATER,
        OTHER
    }

    public enum QualityStatus {
        SAFE,
        MODERATE_RISK,
        HIGH_RISK,
        CONTAMINATED
    }
}
