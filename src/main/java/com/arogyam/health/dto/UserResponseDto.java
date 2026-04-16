package com.arogyam.health.dto;

import com.arogyam.health.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private UserRole role;
    private String district;
    private String state;
    private Long villageId;
    private String villageName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Default constructor
    public UserResponseDto() {
    }

    // All arguments constructor
    public UserResponseDto(Long id, String username, String fullName, String phoneNumber,
                           String email, UserRole role, String district, String state, Long villageId,
                           String villageName, Boolean isActive, LocalDateTime createdAt,
                           LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.district = district;
        this.state = state;
        this.villageId = villageId;
        this.villageName = villageName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Builder pattern implementation
    public static UserResponseDtoBuilder builder() {
        return new UserResponseDtoBuilder();
    }

    // Builder class
    public static class UserResponseDtoBuilder {
        private Long id;
        private String username;
        private String fullName;
        private String phoneNumber;
        private String email;
        private UserRole role;
        private String district;
        private String state;
        private Long villageId;
        private String villageName;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;

        public UserResponseDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserResponseDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserResponseDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserResponseDtoBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserResponseDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserResponseDtoBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserResponseDtoBuilder district(String district) {
            this.district = district;
            return this;
        }

        public UserResponseDtoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public UserResponseDtoBuilder villageId(Long villageId) {
            this.villageId = villageId;
            return this;
        }

        public UserResponseDtoBuilder villageName(String villageName) {
            this.villageName = villageName;
            return this;
        }

        public UserResponseDtoBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserResponseDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserResponseDtoBuilder lastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public UserResponseDto build() {
            return new UserResponseDto(id, username, fullName, phoneNumber, email, role,
                    district, state, villageId, villageName, isActive, createdAt, lastLogin);
        }
    }

    // equals() method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserResponseDto that = (UserResponseDto) obj;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(fullName, that.fullName)) return false;
        if (!Objects.equals(phoneNumber, that.phoneNumber)) return false;
        if (!Objects.equals(email, that.email)) return false;
        if (role != that.role) return false;
        if (!Objects.equals(district, that.district)) return false;
        if (!Objects.equals(state, that.state)) return false;
        if (!Objects.equals(villageId, that.villageId)) return false;
        if (!Objects.equals(villageName, that.villageName)) return false;
        if (!Objects.equals(isActive, that.isActive)) return false;
        if (!Objects.equals(createdAt, that.createdAt)) return false;
        return Objects.equals(lastLogin, that.lastLogin);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (villageId != null ? villageId.hashCode() : 0);
        result = 31 * result + (villageName != null ? villageName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (lastLogin != null ? lastLogin.hashCode() : 0);
        return result;
    }

    // toString() method
    @Override
    public String toString() {
        return "UserResponseDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", villageId=" + villageId +
                ", villageName='" + villageName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
