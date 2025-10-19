package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getOverviewStats() {
        try {
            Map<String, Object> stats = dashboardService.getOverviewStatistics();
            return ResponseEntity.ok(
                    ApiResponseDto.success("Statistics retrieved successfully", stats));
        } catch (Exception e) {
            logger.error("Error retrieving overview statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/district/{district}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getDistrictStats(
            @PathVariable String district) {
        try {
            Map<String, Object> stats = dashboardService.getDistrictStatistics(district);
            return ResponseEntity.ok(
                    ApiResponseDto.success("District statistics retrieved successfully", stats));
        } catch (Exception e) {
            logger.error("Error retrieving district statistics for: {}", district, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve district statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/village/{villageId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getVillageStats(
            @PathVariable Long villageId) {
        try {
            Map<String, Object> stats = dashboardService.getVillageStatistics(villageId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Village statistics retrieved successfully", stats));
        } catch (Exception e) {
            logger.error("Error retrieving village statistics for: {}", villageId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve village statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/health-trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getHealthTrends(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) String district) {
        try {
            Map<String, Object> trends = dashboardService.getHealthTrends(days, district);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Health trends retrieved successfully", trends));
        } catch (Exception e) {
            logger.error("Error retrieving health trends", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve health trends: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/water-quality")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getWaterQualityTrends(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) String district) {
        try {
            Map<String, Object> trends = dashboardService.getWaterQualityTrends(days, district);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Water quality trends retrieved successfully", trends));
        } catch (Exception e) {
            logger.error("Error retrieving water quality trends", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve water quality trends: " + e.getMessage()));
        }
    }

    @GetMapping("/public/health-advisory")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getPublicHealthAdvisory() {
        try {
            Map<String, Object> advisory = dashboardService.getPublicHealthAdvisory();
            return ResponseEntity.ok(
                    ApiResponseDto.success("Health advisory retrieved successfully", advisory));
        } catch (Exception e) {
            logger.error("Error retrieving public health advisory", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve health advisory: " + e.getMessage()));
        }
    }

    @GetMapping("/alerts/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('HEALTH_WORKER')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getActiveAlerts(
            @RequestParam(required = false) String district) {
        try {
            Map<String, Object> alerts = dashboardService.getActiveAlerts(district);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Active alerts retrieved successfully", alerts));
        } catch (Exception e) {
            logger.error("Error retrieving active alerts", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve alerts: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getReportsSummary(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(required = false) String district) {
        try {
            Map<String, Object> summary = dashboardService.getReportsSummary(days, district);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Reports summary retrieved successfully", summary));
        } catch (Exception e) {
            logger.error("Error retrieving reports summary", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports summary: " + e.getMessage()));
        }
    }
}