package com.arogyam.health.service;

import com.arogyam.health.entity.AlertEntity;
import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.entity.WaterQualityEntity;
import com.arogyam.health.repository.AlertRepository;
import com.arogyam.health.repository.HealthReportRepository;
import com.arogyam.health.repository.VillageRepository;
import com.arogyam.health.repository.WaterQualityReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private HealthReportRepository healthReportRepository;

    @Autowired
    private WaterQualityReportRepository waterQualityRepository;

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private AlertRepository alertRepository;

    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total counts
        stats.put("totalVillages", villageRepository.count());
        stats.put("totalHealthReports", healthReportRepository.count());
        stats.put("totalWaterTests", waterQualityRepository.count());
        stats.put("activeAlerts", alertRepository.findByIsActiveTrue().size());

        // Recent reports (last 7 days)
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<HealthReportEntity> recentReports = healthReportRepository
                .findReportsBetweenDates(sevenDaysAgo, LocalDate.now());
        stats.put("recentReports", recentReports.size());

        // Severity breakdown
        Map<String, Long> severityBreakdown = recentReports.stream()
                .collect(Collectors.groupingBy(
                        report -> report.getSeverityLevel().toString(),
                        Collectors.counting()
                ));
        stats.put("severityBreakdown", severityBreakdown);

        // Critical alerts
        List<AlertEntity> criticalAlerts = alertRepository
                .findByPriorityAndIsActiveTrue(AlertEntity.AlertPriority.CRITICAL);
        stats.put("criticalAlerts", criticalAlerts.size());

        // Unverified reports
        List<HealthReportEntity> unverifiedReports = healthReportRepository.findByIsVerifiedFalse();
        stats.put("unverifiedReports", unverifiedReports.size());

        return stats;
    }

    public Map<String, Object> getDistrictStatistics(String district) {
        Map<String, Object> stats = new HashMap<>();

        // District villages
        List<VillageEntity> villages = villageRepository.findByDistrict(district);
        stats.put("totalVillages", villages.size());
        stats.put("villages", villages.stream()
                .map(v -> Map.of("id", v.getId(), "name", v.getName()))
                .collect(Collectors.toList()));

        // Health reports in district (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<HealthReportEntity> districtReports = healthReportRepository
                .findRecentReportsByDistrict(district, thirtyDaysAgo);
        stats.put("totalReports", districtReports.size());

        // Severity distribution
        Map<String, Long> severityDist = districtReports.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSeverityLevel().toString(),
                        Collectors.counting()
                ));
        stats.put("severityDistribution", severityDist);

        // Top symptoms
        Map<String, Long> topSymptoms = districtReports.stream()
                .filter(r -> r.getSymptoms() != null)
                .flatMap(r -> r.getSymptoms().stream())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedSymptoms = topSymptoms.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        stats.put("topSymptoms", sortedSymptoms);

        // Active alerts
        List<AlertEntity> districtAlerts = alertRepository.findActiveAlertsByDistrict(district);
        stats.put("activeAlerts", districtAlerts.size());

        // Water quality status
        List<WaterQualityEntity> waterTests = waterQualityRepository
                .findRecentTestsByDistrict(district, LocalDateTime.now().minusDays(30));

        Map<String, Long> waterQualityDist = waterTests.stream()
                .collect(Collectors.groupingBy(
                        w -> w.getQualityStatus().toString(),
                        Collectors.counting()
                ));
        stats.put("waterQualityDistribution", waterQualityDist);

        return stats;
    }

    public Map<String, Object> getVillageStatistics(Long villageId) {
        Map<String, Object> stats = new HashMap<>();

        VillageEntity village = villageRepository.findById(villageId)
                .orElseThrow(() -> new RuntimeException("Village not found"));

        stats.put("villageName", village.getName());
        stats.put("district", village.getDistrict());
        stats.put("state", village.getState());

        // Health reports count
        List<HealthReportEntity> villageReports = healthReportRepository.findByVillageId(villageId);
        stats.put("totalReports", villageReports.size());

        // Recent reports (last 7 days)
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        Long recentCount = healthReportRepository.countRecentReportsByVillage(villageId, sevenDaysAgo);
        stats.put("recentReports", recentCount);

        // Severity breakdown
        Map<String, Long> severityBreakdown = villageReports.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSeverityLevel().toString(),
                        Collectors.counting()
                ));
        stats.put("severityBreakdown", severityBreakdown);

        // Active alerts
        List<AlertEntity> villageAlerts = alertRepository.findByVillageIdAndIsActiveTrue(villageId);
        stats.put("activeAlerts", villageAlerts.size());

        // Unread alerts
        Long unreadAlerts = alertRepository.countUnreadAlertsByVillage(villageId);
        stats.put("unreadAlerts", unreadAlerts);

        // Water quality
        List<WaterQualityEntity> waterTests = waterQualityRepository.findByVillageId(villageId);
        if (!waterTests.isEmpty()) {
            WaterQualityEntity latestTest = waterTests.get(waterTests.size() - 1);
            stats.put("latestWaterQuality", latestTest.getQualityStatus().toString());
        }

        return stats;
    }

    public Map<String, Object> getHealthTrends(int days, String district) {
        Map<String, Object> trends = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusDays(days);
        List<HealthReportEntity> reports;

        if (district != null && !district.isEmpty()) {
            reports = healthReportRepository.findRecentReportsByDistrict(district, startDate);
        } else {
            reports = healthReportRepository.findReportsBetweenDates(startDate, LocalDate.now());
        }

        // Group by date
        Map<LocalDate, Long> dailyReports = reports.stream()
                .collect(Collectors.groupingBy(
                        HealthReportEntity::getReportDate,
                        Collectors.counting()
                ));
        trends.put("dailyReports", dailyReports);

        // Group by severity over time
        Map<String, Map<LocalDate, Long>> severityTrends = reports.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSeverityLevel().toString(),
                        Collectors.groupingBy(
                                HealthReportEntity::getReportDate,
                                Collectors.counting()
                        )
                ));
        trends.put("severityTrends", severityTrends);

        return trends;
    }

    public Map<String, Object> getWaterQualityTrends(int days, String district) {
        Map<String, Object> trends = new HashMap<>();

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<WaterQualityEntity> tests;

        if (district != null && !district.isEmpty()) {
            tests = waterQualityRepository.findRecentTestsByDistrict(district, startDate);
        } else {
            tests = waterQualityRepository.findAll();
        }

        // Quality status distribution over time
        Map<String, Long> qualityDistribution = tests.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getQualityStatus().toString(),
                        Collectors.counting()
                ));
        trends.put("qualityDistribution", qualityDistribution);

        // Source type distribution
        Map<String, Long> sourceDistribution = tests.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getSourceType().toString(),
                        Collectors.counting()
                ));
        trends.put("sourceDistribution", sourceDistribution);

        return trends;
    }

    public Map<String, Object> getPublicHealthAdvisory() {
        Map<String, Object> advisory = new HashMap<>();

        // Get active high-priority alerts
        List<AlertEntity> criticalAlerts = alertRepository
                .findByPriorityAndIsActiveTrue(AlertEntity.AlertPriority.CRITICAL);
        List<AlertEntity> highAlerts = alertRepository
                .findByPriorityAndIsActiveTrue(AlertEntity.AlertPriority.HIGH);

        advisory.put("criticalAlerts", criticalAlerts.stream()
                .map(a -> Map.of(
                        "title", a.getTitle(),
                        "message", a.getMessage(),
                        "type", a.getType().toString()
                ))
                .collect(Collectors.toList()));

        advisory.put("highPriorityAlerts", highAlerts.stream()
                .map(a -> Map.of(
                        "title", a.getTitle(),
                        "message", a.getMessage(),
                        "type", a.getType().toString()
                ))
                .collect(Collectors.toList()));

        return advisory;
    }

    public Map<String, Object> getActiveAlerts(String district) {
        Map<String, Object> alertsData = new HashMap<>();

        List<AlertEntity> alerts;
        if (district != null && !district.isEmpty()) {
            alerts = alertRepository.findActiveAlertsByDistrict(district);
        } else {
            alerts = alertRepository.findByIsActiveTrue();
        }

        // Group by priority
        Map<String, Long> priorityCount = alerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPriority().toString(),
                        Collectors.counting()
                ));
        alertsData.put("priorityDistribution", priorityCount);

        // Group by type
        Map<String, Long> typeCount = alerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().toString(),
                        Collectors.counting()
                ));
        alertsData.put("typeDistribution", typeCount);

        alertsData.put("totalAlerts", alerts.size());

        return alertsData;
    }

    public Map<String, Object> getReportsSummary(int days, String district) {
        Map<String, Object> summary = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusDays(days);
        List<HealthReportEntity> reports;

        if (district != null && !district.isEmpty()) {
            reports = healthReportRepository.findRecentReportsByDistrict(district, startDate);
        } else {
            reports = healthReportRepository.findReportsBetweenDates(startDate, LocalDate.now());
        }

        summary.put("totalReports", reports.size());
        summary.put("verifiedReports", reports.stream().filter(HealthReportEntity::getIsVerified).count());
        summary.put("unverifiedReports", reports.stream().filter(r -> !r.getIsVerified()).count());

        // Severity counts
        Map<String, Long> severityCounts = reports.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSeverityLevel().toString(),
                        Collectors.counting()
                ));
        summary.put("severityCounts", severityCounts);

        return summary;
    }
}