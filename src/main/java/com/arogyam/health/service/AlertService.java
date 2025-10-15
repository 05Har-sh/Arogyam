package com.arogyam.health.service;

import com.arogyam.health.entity.AlertEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    // Create new alert
    public AlertEntity createAlert(AlertEntity alert) {
        return alertRepository.save(alert);
    }

    // Get all active alerts
    public List<AlertEntity> getAllActiveAlerts() {
        return alertRepository.findByIsActiveTrue();
    }

    // Get alert by ID
    public AlertEntity getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    // Get alerts by village
    public List<AlertEntity> getAlertsByVillage(Long villageId) {
        return alertRepository.findByVillageIdAndIsActiveTrue(villageId);
    }

    // Get alerts by type
    public List<AlertEntity> getAlertsByType(AlertEntity.AlertType type) {
        return alertRepository.findByTypeAndIsActiveTrue(type);
    }

    // Get alerts by priority
    public List<AlertEntity> getAlertsByPriority(AlertEntity.AlertPriority priority) {
        return alertRepository.findByPriorityAndIsActiveTrue(priority);
    }

    // Get alerts by district
    public List<AlertEntity> getAlertsByDistrict(String district) {
        return alertRepository.findActiveAlertsByDistrict(district);
    }

    // Mark alert as read
    public void markAlertAsRead(Long alertId) {
        AlertEntity alert = getAlertById(alertId);
        alert.setIsRead(true);
        alertRepository.save(alert);
    }

    // Deactivate alert
    public void deactivateAlert(Long alertId) {
        AlertEntity alert = getAlertById(alertId);
        alert.setIsActive(false);
        alertRepository.save(alert);
    }

    // Count unread alerts for a village
    public Long countUnreadAlerts(Long villageId) {
        return alertRepository.countUnreadAlertsByVillage(villageId);
    }

    // Get critical alerts
    public List<AlertEntity> getCriticalAlerts() {
        return alertRepository.findByPriorityAndIsActiveTrue(AlertEntity.AlertPriority.CRITICAL);
    }

    // Get recent alerts
    public List<AlertEntity> getRecentAlerts(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return alertRepository.findByPriorityInAndCreatedAtAfter(
                List.of(AlertEntity.AlertPriority.HIGH, AlertEntity.AlertPriority.CRITICAL),
                startDate
        );
    }
}