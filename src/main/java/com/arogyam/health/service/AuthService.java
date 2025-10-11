package com.arogyam.health.service;

import com.arogyam.health.dto.LoginRequestDto;
import com.arogyam.health.dto.LoginResponseDto;
import com.arogyam.health.dto.UserRegistrationDto;
import com.arogyam.health.entity.UserEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.exception.UnauthorizedException;
import com.arogyam.health.repository.UserRepository;
import com.arogyam.health.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return new LoginResponseDto(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole().toString(),
                    user.getDistrict(),
                    user.getVillage() != null ? user.getVillage().getId() : null,
                    user.getVillage() != null ? user.getVillage().getName() : null
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public UserEntity register(UserRegistrationDto registrationDto) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already registered!");
        }

        // Create new user
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole(UserEntity.UserRole.valueOf(registrationDto.getRole()));
        user.setDistrict(registrationDto.getDistrict());

        return userRepository.save(user);
    }

    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
