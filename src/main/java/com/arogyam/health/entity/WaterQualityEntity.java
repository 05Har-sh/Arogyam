package com.arogyam.health.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal; // <-- NEW IMPORT
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

    // FIX: Changed type from Double to BigDecimal to support precision/scale
    @DecimalMin(value = "0.0", message = "pH level must be atleast 0")
    @DecimalMax(value = "14.4", message = "pH level cannot exceed 14")
    @Column(precision = 4, scale = 2)
    private BigDecimal phLevel;

    // FIX: Changed type from Double to BigDecimal to support precision/scale
    @DecimalMin(value = "0.0", message = "Turbidity must be non-negative")
    @Column(precision = 4, scale = 2)
    private BigDecimal turbidity;

    @Min(value = 0, message = "Bacterial count must be non-negative")
    private Integer bacterialCount;

    // FIX: Changed type from Double to BigDecimal to support precision/scale
    @DecimalMin(value = "-10.0", message = "Temperature must be at least -10°C")
    @DecimalMax(value = "100.0", message = "Temperature cannot exceed 100°C")
    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

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

    // FIX: Changed type from Double to BigDecimal to support precision/scale
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    // FIX: Changed type from Double to BigDecimal to support precision/scale
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

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

    // Getters and Setters (Updated for BigDecimal)
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

    // Updated getter/setter for BigDecimal
    public BigDecimal getPhLevel() {
        return phLevel;
    }

    // Updated getter/setter for BigDecimal
    public void setPhLevel(BigDecimal phLevel) {
        this.phLevel = phLevel;
    }

    // Updated getter/setter for BigDecimal
    public BigDecimal getTurbidity() {
        return turbidity;
    }

    // Updated getter/setter for BigDecimal
    public void setTurbidity(BigDecimal turbidity) {
        this.turbidity = turbidity;
    }

    public Integer getBacterialCount() {
        return bacterialCount;
    }

    public void setBacterialCount(Integer bacterialCount) {
        this.bacterialCount = bacterialCount;
    }

    // Updated getter/setter for BigDecimal
    public BigDecimal getTemperature() {
        return temperature;
    }

    // Updated getter/setter for BigDecimal
    public void setTemperature(BigDecimal temperature) {
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

    // Updated getter/setter for BigDecimal
    public BigDecimal getLatitude() {
        return latitude;
    }

    // Updated getter/setter for BigDecimal
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    // Updated getter/setter for BigDecimal
    public BigDecimal getLongitude() {
        return longitude;
    }

    // Updated getter/setter for BigDecimal
    public void setLongitude(BigDecimal longitude) {
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
