package com.arogyam.health.controller;

import com.arogyam.health.dto.AlertDto;
import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.entity.AlertEntity;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.repository.VillageRepository;
import com.arogyam.health.service.AlertService;
import com.arogyam.health.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private VillageRepository villageRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<AlertDto>> createAlert(
            @Valid @RequestBody AlertDto dto,
            Authentication authentication) {
        UserEntity createdBy = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AlertEntity alert = new AlertEntity();
        alert.setType(dto.getType());
        alert.setTitle(dto.getTitle());
        alert.setMessage(dto.getMessage());
        alert.setPriority(dto.getPriority());
        alert.setCreatedBy(createdBy);
        if (dto.getVillageId() != null) {
            VillageEntity village = villageRepository.findById(dto.getVillageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Village not found"));
            alert.setVillage(village);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Alert created successfully", toDto(alertService.createAlert(alert))));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('HEALTH_WORKER') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<List<AlertDto>>> getActiveAlerts(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Long villageId) {
        List<AlertEntity> alerts = villageId != null ? alertService.getAlertsByVillage(villageId)
                : (district != null ? alertService.getAlertsByDistrict(district) : alertService.getAllActiveAlerts());
        return ResponseEntity.ok(ApiResponseDto.success("Active alerts retrieved successfully",
                alerts.stream().map(this::toDto).toList()));
    }

    @PutMapping("/{alertId}/read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('HEALTH_WORKER') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<Void>> markAsRead(@PathVariable Long alertId) {
        alertService.markAlertAsRead(alertId);
        return ResponseEntity.ok(ApiResponseDto.success("Alert marked as read"));
    }

    @PutMapping("/{alertId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<Void>> deactivate(@PathVariable Long alertId) {
        alertService.deactivateAlert(alertId);
        return ResponseEntity.ok(ApiResponseDto.success("Alert deactivated"));
    }

    private AlertDto toDto(AlertEntity alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setType(alert.getType());
        dto.setTitle(alert.getTitle());
        dto.setMessage(alert.getMessage());
        dto.setPriority(alert.getPriority());
        dto.setVillageId(alert.getVillage() != null ? alert.getVillage().getId() : null);
        dto.setVillageName(alert.getVillage() != null ? alert.getVillage().getName() : null);
        dto.setCreatedByUserId(alert.getCreatedBy() != null ? alert.getCreatedBy().getId() : null);
        dto.setCreatedByUsername(alert.getCreatedBy() != null ? alert.getCreatedBy().getUsername() : null);
        dto.setIsActive(alert.getIsActive());
        dto.setIsRead(alert.getIsRead());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }
}
