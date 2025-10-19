package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.WaterQualityDto;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.WaterQualityEntity;
import com.arogyam.health.service.UserService;
import com.arogyam.health.service.WaterQualityService;
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
@RequestMapping("/api/water-quality")
@CrossOrigin(origins = "*")
public class WaterQualityController {

    private static final Logger logger = LoggerFactory.getLogger(WaterQualityController.class);

    @Autowired
    private WaterQualityService waterQualityService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('FIELD_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<WaterQualityEntity>> createWaterQualityReport(
            @Valid @RequestBody WaterQualityDto reportDto,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity tester = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            WaterQualityEntity report = waterQualityService.createWaterQualityReport(reportDto, tester.getId());
            logger.info("Water quality report created by user: {}", username);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Water quality report created successfully", report));
        } catch (Exception e) {
            logger.error("Error creating water quality report", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to create water quality report: " + e.getMessage()));
        }
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<WaterQualityEntity>> getReportById(@PathVariable Long reportId) {
        try {
            WaterQualityEntity report = waterQualityService.getReportById(reportId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Water quality report retrieved successfully", report));
        } catch (Exception e) {
            logger.error("Error retrieving water quality report: {}", reportId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Water quality report not found"));
        }
    }

    @GetMapping("/village/{villageId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getReportsByVillage(
            @PathVariable Long villageId) {
        try {
            List<WaterQualityEntity> reports = waterQualityService.getReportsByVillage(villageId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Village water quality reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving water quality reports for village: {}", villageId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports: " + e.getMessage()));
        }
    }

    @GetMapping("/tester/{testerId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('FIELD_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getReportsByTester(
            @PathVariable Long testerId) {
        try {
            List<WaterQualityEntity> reports = waterQualityService.getReportsByTester(testerId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Tester reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving reports for tester: {}", testerId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports: " + e.getMessage()));
        }
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('FIELD_AGENT')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getMyReports(
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserEntity tester = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<WaterQualityEntity> reports = waterQualityService.getReportsByTester(tester.getId());
            return ResponseEntity.ok(
                    ApiResponseDto.success("Your water quality reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving user water quality reports", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve your reports: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getReportsByStatus(
            @PathVariable String status) {
        try {
            WaterQualityEntity.QualityStatus qualityStatus;
            try {
                qualityStatus = WaterQualityEntity.QualityStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid status. Valid values: SAFE, MODERATE_RISK, HIGH_RISK, CONTAMINATED"));
            }

            List<WaterQualityEntity> reports = waterQualityService.getReportsByStatus(qualityStatus);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Reports by status retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving reports by status: {}", status, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports: " + e.getMessage()));
        }
    }

    @GetMapping("/district/{district}/recent")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('ANALYST')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getRecentReportsByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<WaterQualityEntity> reports = waterQualityService.getRecentReportsByDistrict(district, days);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Recent district reports retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving recent reports for district: {}", district, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve reports: " + e.getMessage()));
        }
    }

    @GetMapping("/contaminated")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('ANALYST')")
    public ResponseEntity<ApiResponseDto<List<WaterQualityEntity>>> getContaminatedSources(
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<WaterQualityEntity> reports = waterQualityService.getContaminatedSources(days);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Contaminated sources retrieved successfully", reports));
        } catch (Exception e) {
            logger.error("Error retrieving contaminated sources", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve contaminated sources: " + e.getMessage()));
        }
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<WaterQualityEntity>> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody WaterQualityDto reportDto,
            Authentication authentication) {
        try {
            WaterQualityEntity updatedReport = waterQualityService.updateReport(reportId, reportDto);
            logger.info("Water quality report {} updated by user: {}", reportId, authentication.getName());

            return ResponseEntity.ok(
                    ApiResponseDto.success("Water quality report updated successfully", updatedReport));
        } catch (Exception e) {
            logger.error("Error updating water quality report: {}", reportId, e);
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
            waterQualityService.deleteReport(reportId);
            logger.info("Water quality report {} deleted by user: {}", reportId, authentication.getName());

            return ResponseEntity.ok(
                    ApiResponseDto.success("Water quality report deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting water quality report: {}", reportId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to delete report: " + e.getMessage()));
        }
    }

    @GetMapping("/village/{villageId}/count")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Long>> getReportCountByVillage(@PathVariable Long villageId) {
        try {
            Long count = waterQualityService.getReportCountByVillage(villageId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Report count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Error getting report count for village: {}", villageId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to get report count: " + e.getMessage()));
        }
    }

    @GetMapping("/village/{villageId}/latest")
    @PreAuthorize("hasRole('HEALTH_WORKER') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<WaterQualityEntity>> getLatestReportByVillage(
            @PathVariable Long villageId) {
        try {
            WaterQualityEntity report = waterQualityService.getLatestReportByVillage(villageId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("Latest report retrieved successfully", report));
        } catch (Exception e) {
            logger.error("Error getting latest report for village: {}", villageId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("No reports found for this village"));
        }
    }
}