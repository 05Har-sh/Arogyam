package com.arogyam.health.dto;

import com.arogyam.health.entity.UserRole;
import jakarta.validation.constraints.*;

public class UserRegistrationDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")  // Fixed typo: "requied" -> "required"
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    private Long villageId;

    // Optional legacy field for compatibility
    @Size(max = 100, message = "Village must not exceed 100 characters")
    private String village;

    // Optional: If you still want to support village by ID, keep this field too
    // But make sure your service handles both cases
    // private Long villageId;

    // Default constructor
    public UserRegistrationDto() {
    }

    // All arguments constructor
    public UserRegistrationDto(String username, String password, String fullName, String phoneNumber,
                               String email, UserRole role, String district, String state, Long villageId, String village) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.district = district;
        this.state = state;
        this.villageId = villageId;
        this.village = village;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    // Builder pattern implementation
    public static UserRegistrationDtoBuilder builder() {
        return new UserRegistrationDtoBuilder();
    }

    // Builder class
    public static class UserRegistrationDtoBuilder {
        private String username;
        private String password;
        private String fullName;
        private String phoneNumber;
        private String email;
        private UserRole role;
        private String district;
        private String state;
        private Long villageId;
        private String village;

        public UserRegistrationDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserRegistrationDtoBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserRegistrationDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserRegistrationDtoBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserRegistrationDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRegistrationDtoBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserRegistrationDtoBuilder district(String district) {
            this.district = district;
            return this;
        }

        public UserRegistrationDtoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public UserRegistrationDtoBuilder villageId(Long villageId) {
            this.villageId = villageId;
            return this;
        }

        public UserRegistrationDtoBuilder village(String village) {
            this.village = village;
            return this;
        }

        public UserRegistrationDto build() {
            return new UserRegistrationDto(username, password, fullName, phoneNumber,
                    email, role, district, state, villageId, village);
        }
    }

    // equals() method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserRegistrationDto that = (UserRegistrationDto) obj;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (role != that.role) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (villageId != null ? !villageId.equals(that.villageId) : that.villageId != null) return false;
        return village != null ? village.equals(that.village) : that.village == null;
    }

    // hashCode() method
    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (villageId != null ? villageId.hashCode() : 0);
        result = 31 * result + (village != null ? village.hashCode() : 0);
        return result;
    }

    // toString() method
    @Override
    public String toString() {
        return "UserRegistrationDto{" +
                "username='" + username + '\'' +
                ", password='" + "[PROTECTED]" + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", villageId=" + villageId +
                ", village='" + village + '\'' +
                '}';
    }
}
