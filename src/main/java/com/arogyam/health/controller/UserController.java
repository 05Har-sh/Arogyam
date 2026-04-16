package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.UserRegistrationDto;
import com.arogyam.health.dto.UserResponseDto;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.UserRole;
import com.arogyam.health.security.UserPrincipal;
import com.arogyam.health.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("User not authenticated"));
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // More efficient: use findByUsername instead of filtering all users
            Optional<UserEntity> userEntity = userService.findByUsername(userPrincipal.getUsername());
            if (userEntity.isEmpty()) {
                logger.warn("Current user not found: {}", userPrincipal.getUsername());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("Current user not found"));
            }

            // Convert to DTO using service method (assuming you have a conversion method)
            UserResponseDto user = convertToResponseDto(userEntity.get());

            return ResponseEntity.ok(ApiResponseDto.success("Current user retrieved", user));

        } catch (ClassCastException e) {
            logger.error("Invalid authentication principal type", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("Invalid authentication"));
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to get current user"));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        try {
            List<UserResponseDto> users = userService.getAllUsers();
            logger.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(ApiResponseDto.success("Users retrieved successfully", users));
        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve users"));
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getActiveUsers() {
        try {
            // Using the new method from updated UserService
            List<UserResponseDto> users = userService.getActiveUsers();
            return ResponseEntity.ok(ApiResponseDto.success("Active users retrieved successfully", users));
        } catch (Exception e) {
            logger.error("Error retrieving active users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve active users"));
        }
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getUsersByRole(@PathVariable String role) {
        try {
            if (!StringUtils.hasText(role)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Role parameter is required"));
            }

            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role requested: {}", role);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Invalid role: " + role + ". Valid roles are: " +
                                String.join(", ", getValidRoles())));
            }

            List<UserResponseDto> users = userService.getUsersByRole(userRole);
            logger.info("Retrieved {} users with role {}", users.size(), userRole);
            return ResponseEntity.ok(ApiResponseDto.success("Users by role retrieved successfully", users));

        } catch (Exception e) {
            logger.error("Error retrieving users by role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve users by role"));
        }
    }

    @GetMapping("/district/{district}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HEALTH_OFFICIAL')")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getUsersByDistrict(@PathVariable String district) {
        try {
            if (!StringUtils.hasText(district)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("District parameter is required"));
            }

            List<UserResponseDto> users = userService.getUsersByDistrict(district.trim());
            logger.info("Retrieved {} users from district {}", users.size(), district);
            return ResponseEntity.ok(ApiResponseDto.success("Users by district retrieved successfully", users));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Invalid district parameter"));
        } catch (Exception e) {
            logger.error("Error retrieving users by district: {}", district, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve users by district"));
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('HEALTH_OFFICIAL') and @userController.canModifyUser(authentication, #userId))")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRegistrationDto updateDto,
            Authentication authentication) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("User ID is required"));
            }

            if (updateDto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Update data is required"));
            }

            UserResponseDto updatedUser = userService.updateUser(userId, updateDto);
            logger.info("User {} updated by {}", userId, authentication.getName());
            return ResponseEntity.ok(ApiResponseDto.success("User updated successfully", updatedUser));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("User not found"));
            }
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.error("Phone number already exists"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Update failed"));
        } catch (Exception e) {
            logger.error("Error updating user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to update user"));
        }
    }

    @PutMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> deactivateUser(@PathVariable Long userId, Authentication authentication) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("User ID is required"));
            }

            userService.deactivateUser(userId);
            logger.info("User {} deactivated by {}", userId, authentication.getName());
            return ResponseEntity.ok(ApiResponseDto.success("User deactivated successfully"));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("User not found"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Deactivation failed"));
        } catch (Exception e) {
            logger.error("Error deactivating user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to deactivate user"));
        }
    }

    @PutMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> activateUser(@PathVariable Long userId, Authentication authentication) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("User ID is required"));
            }

            userService.activateUser(userId);
            logger.info("User {} activated by {}", userId, authentication.getName());
            return ResponseEntity.ok(ApiResponseDto.success("User activated successfully"));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("User not found"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Activation failed"));
        } catch (Exception e) {
            logger.error("Error activating user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to activate user"));
        }
    }

    @PostMapping("/{userId}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @userController.isCurrentUser(authentication, #userId)")
    public ResponseEntity<ApiResponseDto<Void>> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordDto changePasswordDto,
            Authentication authentication) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("User ID is required"));
            }

            if (changePasswordDto == null || !StringUtils.hasText(changePasswordDto.getOldPassword())
                    || !StringUtils.hasText(changePasswordDto.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Old password and new password are required"));
            }

            boolean success = userService.changePassword(userId,
                    changePasswordDto.getOldPassword(),
                    changePasswordDto.getNewPassword());

            if (success) {
                logger.info("Password changed for user {}", userId);
                return ResponseEntity.ok(ApiResponseDto.success("Password changed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Invalid old password"));
            }

        } catch (Exception e) {
            logger.error("Error changing password for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to change password"));
        }
    }

    // Helper methods for authorization
    public boolean canModifyUser(Authentication authentication, Long userId) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            // Health officials can only modify users in their district
            // This is a simplified check - implement according to your business rules
            return principal.getRole() == UserRole.ADMIN || principal.getId().equals(userId);
        } catch (Exception e) {
            logger.warn("Error checking user modification permission", e);
            return false;
        }
    }

    public boolean isCurrentUser(Authentication authentication, Long userId) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return principal.getId().equals(userId);
        } catch (Exception e) {
            logger.warn("Error checking if current user", e);
            return false;
        }
    }

    // Helper methods
    private String[] getValidRoles() {
        return java.util.Arrays.stream(UserRole.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private UserResponseDto convertToResponseDto(UserEntity user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDistrict(user.getDistrict());
        dto.setState(user.getState());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());

        if (user.getVillage() != null) {
            dto.setVillageId(user.getVillage().getId());
            dto.setVillageName(user.getVillage().getName());
        }

        return dto;
    }

    // DTO for password change
    public static class ChangePasswordDto {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
