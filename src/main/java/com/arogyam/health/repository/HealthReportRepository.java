package com.arogyam.health.repository;

import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.entity.VillageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthReportRepository extends JpaRepository<HealthReportEntity, Long> {

    List<HealthReportEntity> findByVillageId(Long villageId);

    List<HealthReportEntity> findByReporterId(Long reporterId);

    List<HealthReportEntity> findByVillageAndReportDateBetween(VillageEntity village,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);

    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.district = :district " +
            "AND h.reportDate >= :startDate")
    List<HealthReportEntity> findRecentReportsByDistrict(@Param("district") String district,
                                                         @Param("startDate") LocalDateTime startDate);

    @Query(value = "SELECT * FROM health_reports h WHERE " +
            "h.symptoms::text ILIKE CONCAT('%', :symptom, '%') " +
            "AND h.report_date >= :startDate",
            nativeQuery = true)
    List<HealthReportEntity> findBySymptomContaining(@Param("symptom") String symptom,
                                                     @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(h) FROM HealthReportEntity h WHERE h.village.id = :villageId " +
            "AND h.reportDate >= :startDate")
    Long countRecentReportsByVillage(@Param("villageId") Long villageId,
                                     @Param("startDate") LocalDateTime startDate);

    @Query("SELECT h FROM HealthReportEntity h WHERE h.severityLevel = :severity " +
            "AND h.village.district = :district AND h.reportDate >= :startDate")
    List<HealthReportEntity> findBySeverityAndDistrictAndDateAfter(
            @Param("severity") HealthReportEntity.SeverityLevel severity,
            @Param("district") String district,
            @Param("startDate") LocalDateTime startDate);
}