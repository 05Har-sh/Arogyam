package com.arogyam.health.controller;

import com.arogyam.health.dto.ApiResponseDto;
import com.arogyam.health.dto.LoginRequestDto;
import com.arogyam.health.dto.LoginResponseDto;
import com.arogyam.health.dto.UserRegistrationDto;
import com.arogyam.health.dto.UserResponseDto;
import com.arogyam.health.security.JwtTokenProvider;
import com.arogyam.health.service.AuthService;
import com.arogyam.health.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            // Validate input
            if (!StringUtils.hasText(loginRequest.getUsername()) || !StringUtils.hasText(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest("Username and password are required"));
            }
            LoginResponseDto loginResponse = authService.login(loginRequest);

            // Prepare response
            Map<String, Object> response = createLoginResponse(loginResponse.getToken(), loginResponse.getUsername());

            logger.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponseDto.success("Login successful", response));

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for username: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.unauthorized("Invalid credentials"));

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for username: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.unauthorized("Authentication failed"));

        } catch (Exception e) {
            logger.error("Login error for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.serverError("Login failed. Please try again later."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            // Additional validation if needed
            if (registrationDto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest("Registration data is required"));
            }

            UserResponseDto user = userService.createUser(registrationDto);

            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("User registered successfully", user));

        } catch (IllegalArgumentException e) {
            logger.warn("Registration validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.badRequest("Invalid registration data"));

        } catch (RuntimeException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            // Don't expose internal error details to client
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.error("User with this username or phone number already exists"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.badRequest("Registration failed"));

        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.serverError("Registration failed. Please try again later."));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest("Invalid authorization header format"));
            }

            boolean isValid = tokenProvider.validateToken(token);
            Map<String, Object> response = createValidationResponse(token, isValid);

            return ResponseEntity.ok(ApiResponseDto.success("Token validation completed", response));

        } catch (Exception e) {
            logger.error("Token validation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.serverError("Token validation failed"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.error("Invalid authorization header format"));
            }

            if (!tokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDto.error("Invalid or expired token"));
            }

            String username = tokenProvider.getUsernameFromToken(token);
            String newToken = tokenProvider.refreshToken(token);

            Map<String, Object> response = createLoginResponse(newToken, username);

            logger.info("Token refreshed for user: {}", username);
            return ResponseEntity.ok(ApiResponseDto.success("Token refreshed successfully", response));

        } catch (Exception e) {
            logger.error("Token refresh error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);

                // Optionally: Add token to blacklist or perform cleanup
                // tokenBlacklistService.blacklistToken(token);

                logger.info("User {} logged out successfully", username);
                return ResponseEntity.ok(ApiResponseDto.success("Logged out successfully"));
            }

            return ResponseEntity.ok(ApiResponseDto.success("Logged out"));

        } catch (Exception e) {
            logger.error("Logout error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.serverError("Logout failed"));
        }
    }

    // Helper methods
    private String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX_LENGTH);
        }
        return null;
    }

    private Map<String, Object> createLoginResponse(String token, String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("username", username);

        // Add token expiration information using your JwtTokenProvider methods
        try {
            Date expirationDate = tokenProvider.getExpirationDateFromToken(token);
            response.put("expiresAt", expirationDate);

            // Calculate remaining time in seconds
            long expiresInSeconds = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
            response.put("expiresIn", expiresInSeconds);

            // Add user information from token
            response.put("userId", tokenProvider.getUserIdFromToken(token));
            response.put("role", tokenProvider.getRoleFromToken(token));
            response.put("fullName", tokenProvider.getFullNameFromToken(token));

        } catch (Exception e) {
            logger.warn("Could not extract token information: {}", e.getMessage());
        }

        return response;
    }

    private Map<String, Object> createValidationResponse(String token, boolean isValid) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        if (isValid) {
            try {
                response.put("username", tokenProvider.getUsernameFromToken(token));
                response.put("userId", tokenProvider.getUserIdFromToken(token));
                response.put("role", tokenProvider.getRoleFromToken(token));
                response.put("fullName", tokenProvider.getFullNameFromToken(token));
                response.put("expiresAt", tokenProvider.getExpirationDateFromToken(token));
                response.put("isExpired", tokenProvider.isTokenExpired(token));
            } catch (Exception e) {
                logger.warn("Could not extract token claims: {}", e.getMessage());
                response.put("valid", false);
                response.put("error", "Invalid token format");
            }
        }

        return response;
    }
}
