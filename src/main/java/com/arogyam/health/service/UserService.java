package com.arogyam.health.service;

import com.arogyam.health.dto.UserRegistrationDto;
import com.arogyam.health.dto.UserResponseDto;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.entity.UserRole;
import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.repository.UserRepository;
import com.arogyam.health.repository.VillageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VillageRepository villageRepository;

    public UserResponseDto createUser(UserRegistrationDto registrationDto) {
        // Validate input
        if (registrationDto == null) {
            throw new IllegalArgumentException("Registration data cannot be null");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + registrationDto.getUsername());
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + registrationDto.getPhoneNumber());
        }

        // Create new user entity
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setEmail(registrationDto.getEmail());
        user.setRole(registrationDto.getRole());
        user.setDistrict(registrationDto.getDistrict());
        user.setState(registrationDto.getState());
        user.setIsActive(true);
        user.setVillage(resolveVillage(registrationDto));

        UserEntity savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        // Since you don't have findActiveUsersByRole, we'll filter manually
        return userRepository.findAll()
                .stream()
                .filter(user -> role.equals(user.getRole()) && user.getIsActive())
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("District cannot be null or empty");
        }

        return userRepository.findAll()
                .stream()
                .filter(user -> district.equals(user.getDistrict()) && user.getIsActive())
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getIsActive())
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public void updateLastLogin(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public UserResponseDto updateUser(Long userId, UserRegistrationDto updateDto) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (updateDto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if phone number is being changed and if new number already exists
        if (updateDto.getPhoneNumber() != null &&
                !updateDto.getPhoneNumber().equals(user.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(updateDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + updateDto.getPhoneNumber());
        }

        // Update fields (excluding password and username for security)
        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getDistrict() != null) {
            user.setDistrict(updateDto.getDistrict());
        }
        if (updateDto.getState() != null) {
            user.setState(updateDto.getState());
        }
        if (updateDto.getVillageId() != null || updateDto.getVillage() != null) {
            user.setVillage(resolveVillage(updateDto));
        }

        UserEntity savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }

    public void deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }

        // Update to new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    private UserResponseDto convertToResponseDto(UserEntity user) {
        if (user == null) {
            return null;
        }

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

    private VillageEntity resolveVillage(UserRegistrationDto dto) {
        if (dto.getVillageId() != null) {
            return villageRepository.findById(dto.getVillageId())
                    .orElseThrow(() -> new RuntimeException("Village not found with id: " + dto.getVillageId()));
        }
        if (StringUtils.hasText(dto.getVillage()) && StringUtils.hasText(dto.getDistrict())) {
            return villageRepository.findByNameAndDistrict(dto.getVillage().trim(), dto.getDistrict().trim())
                    .orElseThrow(() -> new RuntimeException("Village not found: " + dto.getVillage()));
        }
        throw new IllegalArgumentException("Village ID is required");
    }
}
