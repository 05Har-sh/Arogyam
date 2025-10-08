package com.arogyam.health.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_quality_reports")
@EntityListeners(AuditingEntityListener.class)
public class WaterQualityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType;

    private Double phLevel;

    private Double turbidity;

    private Integer bacterialCount;

    private Double temperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualityStatus qualityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id", nullable = false)
    private UserEntity tester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private VillageEntity village;

    private Double latitude;

    private Double longitude;

    @Column(length = 500)
    private String remarks;

    @CreatedDate
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }

    public Double getPhLevel() { return phLevel; }
    public void setPhLevel(Double phLevel) { this.phLevel = phLevel; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public Integer getBacterialCount() { return bacterialCount; }
    public void setBacterialCount(Integer bacterialCount) { this.bacterialCount = bacterialCount; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public QualityStatus getQualityStatus() { return qualityStatus; }
    public void setQualityStatus(QualityStatus qualityStatus) { this.qualityStatus = qualityStatus; }

    public UserEntity getTester() { return tester; }
    public void setTester(UserEntity tester) { this.tester = tester; }

    public VillageEntity getVillage() { return village; }
    public void setVillage(VillageEntity village) { this.village = village; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTestDate() { return testDate; }
    public void setTestDate(LocalDateTime testDate) { this.testDate = testDate; }

    public enum SourceType {
        WELL, TUBE_WELL, POND, RIVER, LAKE, TAP_WATER, OTHER
    }

    public enum QualityStatus {
        SAFE, MODERATE_RISK, HIGH_RISK, CONTAMINATED
    }
}
