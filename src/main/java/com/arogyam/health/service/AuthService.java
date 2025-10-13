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

import java.time.LocalDateTime;

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
            //authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            //generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            //fetch user details
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            //update last login timestamp
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return LoginResponseDto.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .district(user.getDistrict())
                    .state(user.getState())
                    .villageName(user.getVillage())
                    .build();
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
        //check if email already exists
        if (registrationDto.getEmail() != null && !registrationDto.getEmail().isEmpty()){
            if (userRepository.existsByEmail(registrationDto.getEmail())){
                throw new IllegalArgumentException("Email already registered!");
            }
        }

        // Create new user
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole(registrationDto.getRole());
        user.setDistrict(registrationDto.getDistrict());
        user.setState(registrationDto.getState());
        user.setVillage(registrationDto.getVillage());
        user.setEmail(registrationDto.getEmail());

        return userRepository.save(user);
    }

    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
