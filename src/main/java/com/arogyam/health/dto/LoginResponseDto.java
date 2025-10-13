package com.arogyam.health.dto;

import com.arogyam.health.entity.UserRole;
import java.time.LocalDateTime;
import java.util.Objects;

public class LoginResponseDto {
    private String token;
    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
    private String district;
    private String state;
    private Long villageId;
    private String villageName;
    private LocalDateTime timestamp;

    // Default constructor
    public LoginResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    // All arguments constructor
    public LoginResponseDto(String token, Long userId, String username, String fullName,
                            UserRole role, String district, String state, Long villageId,
                            String villageName) {
        this();
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.district = district;
        this.state = state;
        this.villageId = villageId;
        this.villageName = villageName;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Builder pattern
    public static LoginResponseDtoBuilder builder() {
        return new LoginResponseDtoBuilder();
    }

    public static class LoginResponseDtoBuilder {
        private String token;
        private Long userId;
        private String username;
        private String fullName;
        private UserRole role;
        private String district;
        private String state;
        private Long villageId;
        private String villageName;

        public LoginResponseDtoBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public LoginResponseDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResponseDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public LoginResponseDtoBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public LoginResponseDtoBuilder district(String district) {
            this.district = district;
            return this;
        }

        public LoginResponseDtoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public LoginResponseDtoBuilder villageId(Long villageId) {
            this.villageId = villageId;
            return this;
        }

        public LoginResponseDtoBuilder villageName(String villageName) {
            this.villageName = villageName;
            return this;
        }

        public LoginResponseDto build() {
            return new LoginResponseDto(token, userId, username, fullName, role,
                    district, state, villageId, villageName);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LoginResponseDto that = (LoginResponseDto) obj;
        return Objects.equals(token, that.token) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, userId, username);
    }

    @Override
    public String toString() {
        return "LoginResponseDto{" +
                "token='" + "[PROTECTED]" + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", villageId=" + villageId +
                ", villageName='" + villageName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}