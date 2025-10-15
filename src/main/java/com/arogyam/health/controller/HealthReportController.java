package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.HealthReportDto;
import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.security.UserPrincipal;
import com.arogyam.health.service.HealthReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-reports")
@CrossOrigin(origins = "*")
public class HealthReportController {

    @Autowired
    private HealthReportService healthReportService;

    @PostMapping
    @PreAuthorize("hasRole('ASHA_WORKER') or hasRole('CHW') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> createReport(
            @Valid @RequestBody HealthReportDto reportDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            HealthReportEntity report = healthReportService.createHealthReport(reportDto, userPrincipal.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Health report created successfully", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/village/{villageId}")
    @PreAuthorize("hasRole('CHW') or hasRole('HEALTH_OFFICIAL') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getReportsByVillage(
            @PathVariable Long villageId) {
        try {
            List<HealthReportEntity> reports = healthReportService.getReportsByVillage(villageId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Reports retrieved successfully", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('ASHA_WORKER') or hasRole('CHW')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getMyReports(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<HealthReportEntity> reports = healthReportService.getReportsByReporter(userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Your reports retrieved successfully", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/district/{district}/recent")
    @PreAuthorize("hasRole('HEALTH_OFFICIAL') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> getRecentReportsByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<HealthReportEntity> reports = healthReportService.getRecentReportsByDistrict(district, days);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Recent reports retrieved successfully", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasRole('ASHA_WORKER') or hasRole('CHW') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<HealthReportEntity>> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody HealthReportDto reportDto) {
        try {
            HealthReportEntity updatedReport = healthReportService.updateReport(reportId, reportDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Report updated successfully", updatedReport));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasRole('HEALTH_OFFICIAL') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<String>> deleteReport(@PathVariable Long reportId) {
        try {
            healthReportService.deleteReport(reportId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Report deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('HEALTH_OFFICIAL') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<HealthReportEntity>>> searchBySymptom(
            @RequestParam String symptom,
            @RequestParam(defaultValue = "30") int days) {
        try {
            List<HealthReportEntity> reports = healthReportService.searchBySymptom(symptom, days);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Search results retrieved successfully", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}
