package com.arogyam.health.repository;

import com.arogyam.health.entity.WaterQualityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WaterQualityReportRepository extends JpaRepository<WaterQualityEntity, Long> {

    List<WaterQualityEntity> findByVillageId(Long villageId);

    List<WaterQualityEntity> findByTesterId(Long testerId);

    List<WaterQualityEntity> findByQualityStatus(WaterQualityEntity.QualityStatus status);

    @Query("SELECT w FROM WaterQualityEntity w WHERE w.village.district = :district " +
            "AND w.testDate >= :startDate")
    List<WaterQualityEntity> findRecentTestsByDistrict(@Param("district") String district,
                                                       @Param("startDate") LocalDateTime startDate);

    @Query("SELECT w FROM WaterQualityEntity w WHERE w.qualityStatus IN :statuses " +
            "AND w.testDate >= :startDate")
    List<WaterQualityEntity> findByQualityStatusInAndTestDateAfter(
            @Param("statuses") List<WaterQualityEntity.QualityStatus> statuses,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(w) FROM WaterQualityEntity w WHERE w.village.id = :villageId " +
            "AND w.qualityStatus = :status")
    Long countByVillageAndStatus(@Param("villageId") Long villageId,
                                 @Param("status") WaterQualityEntity.QualityStatus status);
}
