package com.arogyam.health.repository;

import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    List<UserEntity> findByVillageAndRole(String village, UserRole role);

    List<UserEntity> findByDistrictAndRole(String district, UserRole role);

    List<UserEntity> findByIsActiveTrue();

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND u.isActive = true")
    List<UserEntity> findActiveUserByRole(@Param("role") UserRole role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role AND u.district = :district")
    long countByRoleAndDistrict(@Param("role") UserRole role, @Param("district") String district);
}