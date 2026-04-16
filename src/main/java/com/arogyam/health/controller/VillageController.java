package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.VillageDto;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.service.VillageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villages")
public class VillageController {

    @Autowired
    private VillageService villageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<VillageDto>> createVillage(@Valid @RequestBody VillageDto dto) {
        VillageEntity created = villageService.createVillage(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Village created successfully", toDto(created)));
    }

    @GetMapping("/{villageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL') or hasRole('HEALTH_WORKER') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<VillageDto>> getVillageById(@PathVariable Long villageId) {
        return ResponseEntity.ok(ApiResponseDto.success("Village retrieved successfully", toDto(villageService.getById(villageId))));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL') or hasRole('ANALYST') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<List<VillageDto>>> getVillages(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String state) {
        List<VillageEntity> villages = district != null ? villageService.getByDistrict(district)
                : (state != null ? villageService.getByState(state) : villageService.getAll());
        return ResponseEntity.ok(ApiResponseDto.success("Villages retrieved successfully",
                villages.stream().map(this::toDto).toList()));
    }

    @PutMapping("/{villageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<VillageDto>> updateVillage(
            @PathVariable Long villageId,
            @Valid @RequestBody VillageDto dto) {
        VillageEntity updated = villageService.updateVillage(villageId, toEntity(dto));
        return ResponseEntity.ok(ApiResponseDto.success("Village updated successfully", toDto(updated)));
    }

    @DeleteMapping("/{villageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> deleteVillage(@PathVariable Long villageId) {
        villageService.deleteVillage(villageId);
        return ResponseEntity.ok(ApiResponseDto.success("Village deleted successfully"));
    }

    private VillageDto toDto(VillageEntity entity) {
        VillageDto dto = new VillageDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDistrict(entity.getDistrict());
        dto.setState(entity.getState());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setPopulation(entity.getPopulation());
        dto.setPrimaryLanguage(entity.getPrimaryLanguage());
        return dto;
    }

    private VillageEntity toEntity(VillageDto dto) {
        VillageEntity entity = new VillageEntity();
        entity.setName(dto.getName());
        entity.setDistrict(dto.getDistrict());
        entity.setState(dto.getState());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setPopulation(dto.getPopulation());
        entity.setPrimaryLanguage(dto.getPrimaryLanguage());
        return entity;
    }
}
