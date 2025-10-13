package com.arogyam.health.repository;

import com.arogyam.health.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    // Find all active alerts
    List<AlertEntity> findByIsActiveTrue();

    // Find active alerts for a specific village
    List<AlertEntity> findByVillageIdAndIsActiveTrue(Long villageId);

    // Find active alerts by type
    List<AlertEntity> findByTypeAndIsActiveTrue(AlertEntity.AlertType type);

    // Find active alerts by priority
    List<AlertEntity> findByPriorityAndIsActiveTrue(AlertEntity.AlertPriority priority);

    // Find active alerts by district (sorted by most recent)
    @Query("SELECT a FROM AlertEntity a WHERE a.village.district = :district " +
            "AND a.isActive = true ORDER BY a.createdAt DESC")
    List<AlertEntity> findActiveAlertsByDistrict(@Param("district") String district);

    // Find alerts by priority levels and date range
    @Query("SELECT a FROM AlertEntity a WHERE a.priority IN :priorities " +
            "AND a.isActive = true AND a.createdAt >= :startDate")
    List<AlertEntity> findByPriorityInAndCreatedAtAfter(
            @Param("priorities") List<AlertEntity.AlertPriority> priorities,
            @Param("startDate") LocalDateTime startDate);

    // Count unread alerts for a village
    @Query("SELECT COUNT(a) FROM AlertEntity a WHERE a.village.id = :villageId " +
            "AND a.isActive = true AND a.isRead = false")
    Long countUnreadAlertsByVillage(@Param("villageId") Long villageId);
}