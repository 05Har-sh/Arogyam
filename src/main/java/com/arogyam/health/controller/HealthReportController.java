package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.HealthReportDto;
import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.service.HealthReportService;
import com.arogyam.health.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-reports")
@CrossOrigin(origins = "*")
public class HealthReportController {

    private static final Logger logger = LoggerFactory.getLogger(HealthReportController.class);

    @Autowired
    private HealthReportService healthReportService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('FIELD_AGENT') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> createReport(
            @Valid @RequestBody HealthReportDto reportDto,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            HealthReportEntity report = healthReportService.createHealthReport(reportDto, user.getId());
            logger.info("Health report created by user: {}", username);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Health report created successfully", report));
        } catch (Exception e) {
            logger.error("Error creating health report", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to create health report: " + e.getMessage()));
        }
    }

    @GetMapping("/village/{villageId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getReportsByVillage(
            @PathVariable Long villageId) {
        try {
            List<HealthReportEntity> reports = healthReportService.getReportsByVillage(villageId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving reports for village: {}", villageId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports: " + e.getMessage()));
        }
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('FIELD_AGENT') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getMyReports(
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<HealthReportEntity> reports = healthReportService.getReportsByReporter(user.getId());
            return ResponseEntity.ok(
                    ApiResponseDto.success("Your reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving user reports", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve your reports: " + e.getMessage()));
        }
    }

    @GetMapping("/district/{district}/recent")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('ANALYST')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getRecentReportsByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<HealthReportEntity> reports = healthReportService.getRecentReportsByDistrict(district, days);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Recent reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving recent reports for district: {}", district, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve recent reports: " + e.getMessage()));
        }
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> getReportById(@PathVariable Long reportId) {
        try {
            HealthReportEntity report = healthReportService.getReportById(reportId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Report retrieved successfully", report));
        } catch (Exception e) {
            logger.error("Error retrieving report: {}", reportId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Report not found"));
        }
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody HealthReportDto reportDto,
            Authentication authentication) {
        try {
            HealthReportEntity updatedReport = healthReportService.updateReport(reportId, reportDto);
            logger.info("Report {} updated by user: {}", reportId, authentication.getName());

            return ResponseEntity.ok(
                    ApiResponseDto.success("Report updated successfully", updatedReport));
        } catch (Exception e) {
            logger.error("Error updating report: {}", reportId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to update report: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> deleteReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        try {
            healthReportService.deleteReport(reportId);
            logger.info("Report {} deleted by user: {}", reportId, authentication.getName());

            return ResponseEntity.ok(
                    ApiResponseDto.success("Report deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting report: {}", reportId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to delete report: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('ANALYST')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> searchBySymptom(
            @RequestParam String symptom,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<HealthReportEntity> reports = healthReportService.searchBySymptom(symptom, days);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Search results retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error searching reports by symptom: {}", symptom, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to search reports: " + e.getMessage()));
        }
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getUnverifiedReports() {
        try {
            List<HealthReportEntity> reports = healthReportService.getUnverifiedReports();
            return ResponseEntity.ok(
                    ApiResponseDto.success("Unverified reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving unverified reports", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve unverified reports: " + e.getMessage()));
        }
    }

    @PutMapping("/{reportId}/verify")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> verifyReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity doctor = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            HealthReportEntity verifiedReport = healthReportService.verifyReport(reportId, doctor.getId());
            logger.info("Report {} verified by doctor: {}", reportId, username);

            return ResponseEntity.ok(
                    ApiResponseDto.success("Report verified successfully", verifiedReport));
        } catch (Exception e) {
            logger.error("Error verifying report: {}", reportId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to verify report: " + e.getMessage()));
        }
    }

    @GetMapping("/village/{villageId}/count")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Long>> getReportCountByVillage(
            @PathVariable Long villageId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            Long count = healthReportService.getReportCountByVillage(villageId, days);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Report count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Error getting report count for village: {}", villageId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to get report count: " + e.getMessage()));
        }
    }
}