package com.arogyam.health.service;

import com.arogyam.health.dto.WaterQualityDto;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.entity.WaterQualityEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.repository.UserRepository;
import com.arogyam.health.repository.VillageRepository;
import com.arogyam.health.repository.WaterQualityReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WaterQualityService {

    @Autowired
    private WaterQualityReportRepository waterQualityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private AlertService alertService;

    public WaterQualityEntity createWaterQualityReport(WaterQualityDto reportDto, Long testerId) {
        UserEntity tester = userRepository.findById(testerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tester not found"));

        VillageEntity village = villageRepository.findById(reportDto.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found"));

        WaterQualityEntity report = new WaterQualityEntity();
        report.setSourceName(reportDto.getSourceName());
        report.setSourceType(WaterQualityEntity.SourceType.valueOf(reportDto.getSourceType()));
        report.setPhLevel(BigDecimal.valueOf(reportDto.getPhLevel()));
        report.setTurbidity(BigDecimal.valueOf(reportDto.getTurbidity()));
        report.setBacterialCount(reportDto.getBacterialCount());
        report.setTemperature(BigDecimal.valueOf(reportDto.getTemperature()));
        report.setQualityStatus(WaterQualityEntity.QualityStatus.valueOf(reportDto.getQualityStatus()));
        report.setTester(tester);
        report.setVillage(village);
        report.setLatitude(BigDecimal.valueOf(reportDto.getLatitude()));
        report.setLongitude(BigDecimal.valueOf(reportDto.getLongitude()));
        report.setRemarks(reportDto.getRemarks());

        WaterQualityEntity savedReport = waterQualityRepository.save(report);

        // Create alert if water is contaminated or high risk
        if (report.getQualityStatus() == WaterQualityEntity.QualityStatus.CONTAMINATED ||
                report.getQualityStatus() == WaterQualityEntity.QualityStatus.HIGH_RISK) {
            createWaterContaminationAlert(village, report);
        }

        return savedReport;
    }

    public WaterQualityEntity getReportById(Long reportId) {
        return waterQualityRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Water quality report not found with id: " + reportId));
    }

    public List<WaterQualityEntity> getReportsByVillage(Long villageId) {
        return waterQualityRepository.findByVillageId(villageId);
    }

    public List<WaterQualityEntity> getReportsByTester(Long testerId) {
        return waterQualityRepository.findByTesterId(testerId);
    }

    public List<WaterQualityEntity> getReportsByStatus(WaterQualityEntity.QualityStatus status) {
        return waterQualityRepository.findByQualityStatus(status);
    }

    public List<WaterQualityEntity> getRecentReportsByDistrict(String district, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return waterQualityRepository.findRecentTestsByDistrict(district, startDate);
    }

    public List<WaterQualityEntity> getContaminatedSources(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<WaterQualityEntity.QualityStatus> dangerousStatuses = List.of(
                WaterQualityEntity.QualityStatus.CONTAMINATED,
                WaterQualityEntity.QualityStatus.HIGH_RISK
        );
        return waterQualityRepository.findByQualityStatusInAndTestDateAfter(dangerousStatuses, startDate);
    }

    public WaterQualityEntity updateReport(Long reportId, WaterQualityDto reportDto) {
        WaterQualityEntity report = waterQualityRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Water quality report not found"));

        report.setSourceName(reportDto.getSourceName());

        if (reportDto.getSourceType() != null) {
            report.setSourceType(WaterQualityEntity.SourceType.valueOf(reportDto.getSourceType()));
        }

        report.setPhLevel(BigDecimal.valueOf(reportDto.getPhLevel()));
        report.setTurbidity(BigDecimal.valueOf(reportDto.getTurbidity()));
        report.setBacterialCount(reportDto.getBacterialCount());
        report.setTemperature(BigDecimal.valueOf(reportDto.getTemperature()));

        if (reportDto.getQualityStatus() != null) {
            report.setQualityStatus(WaterQualityEntity.QualityStatus.valueOf(reportDto.getQualityStatus()));
        }

        report.setLatitude(BigDecimal.valueOf(reportDto.getLatitude()));
        report.setLongitude(BigDecimal.valueOf(reportDto.getLongitude()));
        report.setRemarks(reportDto.getRemarks());

        return waterQualityRepository.save(report);
    }

    public void deleteReport(Long reportId) {
        if (!waterQualityRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Water quality report not found");
        }
        waterQualityRepository.deleteById(reportId);
    }

    public Long getReportCountByVillage(Long villageId) {
        return waterQualityRepository.countByVillageAndStatus(villageId, null);
    }

    public WaterQualityEntity getLatestReportByVillage(Long villageId) {
        List<WaterQualityEntity> reports = waterQualityRepository.findByVillageId(villageId);
        if (reports.isEmpty()) {
            throw new ResourceNotFoundException("No water quality reports found for this village");
        }
        // Return the most recent report (last in list if sorted by date)
        return reports.stream()
                .max((r1, r2) -> r1.getTestDate().compareTo(r2.getTestDate()))
                .orElseThrow(() -> new ResourceNotFoundException("No reports found"));
    }

    private void createWaterContaminationAlert(VillageEntity village, WaterQualityEntity report) {
        String title = "Water Contamination Alert";
        String message = String.format(
                "Warning: Water source '%s' in %s village has been tested and found to be %s. " +
                        "Quality Status: %s. Please avoid using this water source until further notice.",
                report.getSourceName(),
                village.getName(),
                report.getQualityStatus() == WaterQualityEntity.QualityStatus.CONTAMINATED ? "CONTAMINATED" : "HIGH RISK",
                report.getQualityStatus()
        );

        com.arogyam.health.entity.AlertEntity alert = new com.arogyam.health.entity.AlertEntity();
        alert.setType(com.arogyam.health.entity.AlertEntity.AlertType.WATER_CONTAMINATION);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPriority(report.getQualityStatus() == WaterQualityEntity.QualityStatus.CONTAMINATED ?
                com.arogyam.health.entity.AlertEntity.AlertPriority.CRITICAL :
                com.arogyam.health.entity.AlertEntity.AlertPriority.HIGH);
        alert.setVillage(village);
        alert.setCreatedBy(report.getTester());

        alertService.createAlert(alert);
    }
}