package com.arogyam.health.service;

import com.arogyam.health.entity.*;
import com.arogyam.health.repository.HealthReportRepository;
import com.arogyam.health.repository.VillageRepository;
import com.arogyam.health.repository.WaterQualityReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OutbreakPredictionService {

    @Autowired
    private HealthReportRepository healthReportRepository;

    @Autowired
    private WaterQualityReportRepository waterQualityRepository;

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private AlertService alertService;

    private static final int ANALYSIS_WINDOW_DAYS = 7;
    private static final int OUTBREAK_THRESHOLD = 5; // 5 or more similar cases
    private static final double WATER_RISK_THRESHOLD = 0.7; // 70% contaminated sources

    @Async
    public void analyzeOutbreakRisk(Long villageId) {
        VillageEntity village = villageRepository.findById(villageId).orElse(null);
        if (village == null) return;

        // Get recent health reports (FIXED: using LocalDate)
        LocalDate analysisStart = LocalDate.now().minusDays(ANALYSIS_WINDOW_DAYS);
        LocalDate analysisEnd = LocalDate.now();

        List<HealthReportEntity> recentReports = healthReportRepository
                .findByVillageAndReportDateBetween(village, analysisStart, analysisEnd);

        // Check if enough data to analyze
        if (recentReports.isEmpty()) {
            return;
        }

        // Analyze symptom patterns
        Map<String, Long> symptomFrequency = analyzeSymptomPatterns(recentReports);

        // Check water quality correlation
        double waterRiskFactor = analyzeWaterQualityRisk(village.getDistrict());

        // Calculate outbreak risk score
        double outbreakRisk = calculateOutbreakRisk(
                symptomFrequency,
                waterRiskFactor,
                recentReports.size()
        );

        // Generate alerts if risk is high
        if (outbreakRisk > 0.6) { // 60% risk threshold
            generateOutbreakAlert(village, outbreakRisk, symptomFrequency);
        }
    }

    private Map<String, Long> analyzeSymptomPatterns(List<HealthReportEntity> reports) {
        // FIXED: Symptoms is a List<String>, not a comma-separated string
        return reports.stream()
                .filter(report -> report.getSymptoms() != null)
                .flatMap(report -> report.getSymptoms().stream())
                .map(symptom -> symptom.toLowerCase().trim())
                .collect(Collectors.groupingBy(
                        symptom -> symptom,
                        Collectors.counting()
                ));
    }

    private double analyzeWaterQualityRisk(String district) {
        // FIXED: Using proper district parameter
        if (district == null || district.isEmpty()) {
            return 0.0;
        }

        LocalDate analysisStart = LocalDate.now().minusDays(ANALYSIS_WINDOW_DAYS);

        // Get water quality tests for the district
        List<WaterQualityEntity> recentTests = waterQualityRepository
                .findRecentTestsByDistrict(district,
                        java.time.LocalDateTime.of(analysisStart, java.time.LocalTime.MIN));

        if (recentTests.isEmpty()) {
            return 0.0;
        }

        // Count contaminated or high-risk sources
        long contaminatedSources = recentTests.stream()
                .filter(test ->
                        test.getQualityStatus() == WaterQualityEntity.QualityStatus.CONTAMINATED ||
                                test.getQualityStatus() == WaterQualityEntity.QualityStatus.HIGH_RISK
                )
                .count();

        return (double) contaminatedSources / recentTests.size();
    }

    private double calculateOutbreakRisk(
            Map<String, Long> symptoms,
            double waterRisk,
            int totalReports) {

        if (totalReports == 0 || symptoms.isEmpty()) {
            return 0.0;
        }

        // Find most common symptoms
        long maxSymptomCount = symptoms.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        // Calculate base risk from symptom clustering
        double symptomRisk = Math.min(1.0, (double) maxSymptomCount / OUTBREAK_THRESHOLD);

        // Factor in water quality (70% symptoms, 30% water)
        double combinedRisk = (symptomRisk * 0.7) + (waterRisk * 0.3);

        // Boost risk if multiple different symptoms are present (indicates outbreak)
        if (symptoms.size() > 3) {
            combinedRisk *= 1.2;
        }

        return Math.min(1.0, combinedRisk);
    }

    private void generateOutbreakAlert(
            VillageEntity village,
            double riskScore,
            Map<String, Long> symptoms) {

        // Get most common symptom
        String mostCommonSymptom = symptoms.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");

        // Determine priority based on risk score
        AlertEntity.AlertPriority priority = riskScore > 0.8 ?
                AlertEntity.AlertPriority.CRITICAL :
                AlertEntity.AlertPriority.HIGH;

        // Create alert message
        String title = "Potential Disease Outbreak Detected";
        String message = String.format(
                "Alert: Potential outbreak in %s village (%s district). " +
                        "Risk Score: %.0f%%. Most common symptom: %s. " +
                        "Immediate investigation recommended.",
                village.getName(),
                village.getDistrict(),
                riskScore * 100,
                mostCommonSymptom
        );

        // Create alert entity
        AlertEntity alert = new AlertEntity();
        alert.setType(AlertEntity.AlertType.OUTBREAK_WARNING);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPriority(priority);
        alert.setVillage(village);

        // Get system user (or use a default admin user)
        // For now, we'll set createdBy to null or you can fetch an admin user
        alert.setCreatedBy(null); // TODO: Set to system/admin user

        alertService.createAlert(alert);
    }

    public void performScheduledAnalysis() {
        // This method runs periodically to analyze all villages
        List<VillageEntity> villages = villageRepository.findAll();

        for (VillageEntity village : villages) {
            try {
                analyzeOutbreakRisk(village.getId());
            } catch (Exception e) {
                // Log error but continue with other villages
                System.err.println("Error analyzing village " + village.getName() + ": " + e.getMessage());
            }
        }
    }

    // Additional helper method to get outbreak summary
    public Map<String, Object> getOutbreakSummary(Long villageId) {
        VillageEntity village = villageRepository.findById(villageId).orElse(null);
        if (village == null) {
            return Map.of("error", "Village not found");
        }

        LocalDate analysisStart = LocalDate.now().minusDays(ANALYSIS_WINDOW_DAYS);
        LocalDate analysisEnd = LocalDate.now();

        List<HealthReportEntity> recentReports = healthReportRepository
                .findByVillageAndReportDateBetween(village, analysisStart, analysisEnd);

        Map<String, Long> symptoms = analyzeSymptomPatterns(recentReports);
        double waterRisk = analyzeWaterQualityRisk(village.getDistrict());
        double outbreakRisk = calculateOutbreakRisk(symptoms, waterRisk, recentReports.size());

        return Map.of(
                "villageName", village.getName(),
                "totalReports", recentReports.size(),
                "outbreakRisk", outbreakRisk,
                "waterRisk", waterRisk,
                "topSymptoms", symptoms
        );
    }
}