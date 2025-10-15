package com.arogyam.health.service;

import com.arogyam.health.dto.HealthReportDto;
import com.arogyam.health.entity.HealthReportEntity;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.repository.HealthReportRepository;
import com.arogyam.health.repository.UserRepository;
import com.arogyam.health.repository.VillageRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class HealthReportService {

    @Autowired
    private HealthReportRepository healthReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VillageRepository villageRepository;

    // GeometryFactory for creating Point objects
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public HealthReportEntity createHealthReport(HealthReportDto reportDto, Long reporterId) {
        UserEntity reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));

        VillageEntity village = villageRepository.findById(reportDto.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

        HealthReportEntity report = new HealthReportEntity();
        report.setPatientName(reportDto.getPatientName());
        report.setPatientAge(reportDto.getPatientAge());
        report.setPatientGender(HealthReportEntity.Gender.valueOf(reportDto.getPatientGender()));
        report.setSymptoms(reportDto.getSymptoms());
        report.setSeverityLevel(HealthReportEntity.SeverityLevel.valueOf(reportDto.getSeverityLevel()));
        report.setSuspectedDisease(reportDto.getSuspectedDisease());
        report.setAdditionalNotes(reportDto.getAdditionalNotes());
        report.setReporter(reporter);
        report.setVillage(village);

        // Set report date and time (REQUIRED fields)
        report.setReportDate(reportDto.getReportDate() != null ? reportDto.getReportDate() : LocalDate.now());
        report.setReportTime(reportDto.getReportTime() != null ? reportDto.getReportTime() : LocalTime.now());

        // Set location coordinates (PostGIS Point)
        if (reportDto.getLatitude() != null && reportDto.getLongitude() != null) {
            Point point = geometryFactory.createPoint(
                    new Coordinate(reportDto.getLongitude(), reportDto.getLatitude())
            );
            report.setLocationCoordinates(point);
        }

        HealthReportEntity savedReport = healthReportRepository.save(report);

        // Check for potential outbreak (optional - can be implemented later)
        checkForOutbreak(village.getId());

        return savedReport;
    }

    public List<HealthReportEntity> getReportsByVillage(Long villageId) {
        return healthReportRepository.findByVillageId(villageId);
    }

    public List<HealthReportEntity> getReportsByReporter(Long reporterId) {
        return healthReportRepository.findByReporterId(reporterId);
    }

    public List<HealthReportEntity> getRecentReportsByDistrict(String district, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return healthReportRepository.findRecentReportsByDistrict(district, startDate);
    }

    public HealthReportEntity updateReport(Long reportId, HealthReportDto reportDto) {
        HealthReportEntity report = healthReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Health report not found"));

        report.setPatientName(reportDto.getPatientName());
        report.setPatientAge(reportDto.getPatientAge());

        if (reportDto.getPatientGender() != null) {
            report.setPatientGender(HealthReportEntity.Gender.valueOf(reportDto.getPatientGender()));
        }

        if (reportDto.getSymptoms() != null) {
            report.setSymptoms(reportDto.getSymptoms());
        }

        if (reportDto.getSeverityLevel() != null) {
            report.setSeverityLevel(HealthReportEntity.SeverityLevel.valueOf(reportDto.getSeverityLevel()));
        }

        if (reportDto.getSuspectedDisease() != null) {
            report.setSuspectedDisease(reportDto.getSuspectedDisease());
        }

        report.setAdditionalNotes(reportDto.getAdditionalNotes());

        // Update location if provided
        if (reportDto.getLatitude() != null && reportDto.getLongitude() != null) {
            Point point = geometryFactory.createPoint(
                    new Coordinate(reportDto.getLongitude(), reportDto.getLatitude())
            );
            report.setLocationCoordinates(point);
        }

        return healthReportRepository.save(report);
    }

    public void deleteReport(Long reportId) {
        if (!healthReportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Health report not found");
        }
        healthReportRepository.deleteById(reportId);
    }

    public List<HealthReportEntity> searchBySymptom(String symptom, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return healthReportRepository.findBySymptomContaining(symptom, startDate);
    }

    public Long getReportCountByVillage(Long villageId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return healthReportRepository.countRecentReportsByVillage(villageId, startDate);
    }

    public HealthReportEntity getReportById(Long reportId) {
        return healthReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Health report not found with id: " + reportId));
    }

//    public List<HealthReportEntity> getUnverifiedReports() {
//        return healthReportRepository.findByIsVerifiedFalse();
//    }

    public HealthReportEntity verifyReport(Long reportId, Long doctorId) {
        HealthReportEntity report = getReportById(reportId);
        UserEntity doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        report.setIsVerified(true);
        report.setVerifiedBy(doctor);

        return healthReportRepository.save(report);
    }

    // Placeholder for outbreak detection (implement later)
    private void checkForOutbreak(Long villageId) {
        // Check if multiple similar reports in same area
        // If threshold exceeded, create alert
        // This can be implemented when AlertService is ready
    }
}