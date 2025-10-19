package com.arogyam.health.repository;

import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.entity.VillageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthReportRepository extends JpaRepository<HealthReportEntity, Long> {

    // Find all reports for a specific village
    List<HealthReportEntity> findByVillageId(Long villageId);

    // Find all reports by a specific health worker
    List<HealthReportEntity> findByReporterId(Long reporterId);

    // Find reports for a village within date range
    List<HealthReportEntity> findByVillageAndReportDateBetween(
            VillageEntity village,
            LocalDate startDate,
            LocalDate endDate);

    // Find recent reports in a district
    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.district = :district " +
            "AND h.reportDate >= :startDate ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findRecentReportsByDistrict(
            @Param("district") String district,
            @Param("startDate") LocalDate startDate);

    // Find reports by symptom (PostgreSQL JSONB query)
    @Query(value = "SELECT * FROM health_reports h WHERE " +
            "h.symptoms::text ILIKE CONCAT('%', :symptom, '%') " +
            "AND h.report_date >= :startDate",
            nativeQuery = true)
    List<HealthReportEntity> findBySymptomContaining(
            @Param("symptom") String symptom,
            @Param("startDate") LocalDate startDate);

    // Count recent reports in a village
    @Query("SELECT COUNT(h) FROM HealthReportEntity h WHERE h.village.id = :villageId " +
            "AND h.reportDate >= :startDate")
    Long countRecentReportsByVillage(
            @Param("villageId") Long villageId,
            @Param("startDate") LocalDate startDate);

    // Find reports by severity, district, and date
    @Query("SELECT h FROM HealthReportEntity h WHERE h.severityLevel = :severity " +
            "AND h.village.district = :district AND h.reportDate >= :startDate")
    List<HealthReportEntity> findBySeverityAndDistrictAndDateAfter(
            @Param("severity") HealthReportEntity.SeverityLevel severity,
            @Param("district") String district,
            @Param("startDate") LocalDate startDate);

    // Find unverified reports (needs review)
    List<HealthReportEntity> findByIsVerifiedFalse();

    // Find unverified reports by severity (prioritize severe cases)
    List<HealthReportEntity> findByIsVerifiedFalseAndSeverityLevel(
            HealthReportEntity.SeverityLevel severity);

    // Find verified reports by specific doctor
    List<HealthReportEntity> findByVerifiedById(Long doctorId);

    // Find reports by suspected disease
    List<HealthReportEntity> findBySuspectedDiseaseContainingIgnoreCase(String disease);

    // Find severe cases in a village
    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.id = :villageId " +
            "AND h.severityLevel = 'SEVERE' AND h.reportDate >= :startDate")
    List<HealthReportEntity> findSevereReportsByVillage(
            @Param("villageId") Long villageId,
            @Param("startDate") LocalDate startDate);

    // Count reports by severity level in a district
    @Query("SELECT COUNT(h) FROM HealthReportEntity h WHERE h.village.district = :district " +
            "AND h.severityLevel = :severity AND h.reportDate >= :startDate")
    Long countBySeverityAndDistrict(
            @Param("district") String district,
            @Param("severity") HealthReportEntity.SeverityLevel severity,
            @Param("startDate") LocalDate startDate);

    // Find reports in a specific date range (for dashboard/analytics)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.reportDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findReportsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Find reports with specific suspected disease in a district (outbreak tracking)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.suspectedDisease = :disease " +
            "AND h.village.district = :district AND h.reportDate >= :startDate " +
            "ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findByDiseaseAndDistrict(
            @Param("disease") String disease,
            @Param("district") String district,
            @Param("startDate") LocalDate startDate);

    // Count total reports by reporter (health worker performance)
    Long countByReporterId(Long reporterId);

    // Find reports created in last N days (using createdAt timestamp)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.createdAt >= :sinceDate " +
            "ORDER BY h.createdAt DESC")
    List<HealthReportEntity> findRecentlyCreatedReports(
            @Param("sinceDate") java.time.LocalDateTime sinceDate);

    // Find all unverified severe reports (high priority for doctors)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.isVerified = false " +
            "AND h.severityLevel = 'SEVERE' ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findUnverifiedSevereReports();

    // Count reports by village
    Long countByVillageId(Long villageId);

    // Find reports by district
    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.district = :district " +
            "ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findByDistrict(@Param("district") String district);

    // Find reports by state
    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.state = :state " +
            "ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findByState(@Param("state") String state);

    // Get count of reports by severity level
    Long countBySeverityLevel(HealthReportEntity.SeverityLevel severity);

    // Find reports by gender (for demographic analysis)
    List<HealthReportEntity> findByPatientGender(HealthReportEntity.Gender gender);

    // Find reports by age range
    @Query("SELECT h FROM HealthReportEntity h WHERE h.patientAge BETWEEN :minAge AND :maxAge")
    List<HealthReportEntity> findByAgeRange(
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge);

    // Find recent reports in village (last N days)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.village.id = :villageId " +
            "AND h.reportDate >= :startDate ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findRecentReportsByVillage(
            @Param("villageId") Long villageId,
            @Param("startDate") LocalDate startDate);

    // Count unverified reports
    @Query("SELECT COUNT(h) FROM HealthReportEntity h WHERE h.isVerified = false")
    Long countUnverifiedReports();

    // Get reports by multiple severity levels (for dashboard filtering)
    @Query("SELECT h FROM HealthReportEntity h WHERE h.severityLevel IN :severities " +
            "AND h.reportDate >= :startDate ORDER BY h.reportDate DESC")
    List<HealthReportEntity> findBySeverityLevelInAndReportDateAfter(
            @Param("severities") List<HealthReportEntity.SeverityLevel> severities,
            @Param("startDate") LocalDate startDate);
}