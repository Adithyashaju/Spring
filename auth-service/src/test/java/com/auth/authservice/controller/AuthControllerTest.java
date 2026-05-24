package com.auth.authservice.controller;

import com.auth.authservice.dto.AuthResponse;
import com.auth.authservice.dto.LoginRequest;
import com.auth.authservice.dto.LogoutRequest;
import com.auth.authservice.dto.RegisterRequest;
import com.auth.authservice.exception.BadRequestException;
import com.auth.authservice.exception.ResourceNotFoundException;
import com.auth.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("secret123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("secret123");
    }

    // --- register ---

    @Test
    void register_Returns200WithSuccessMessage() {
        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

        var response = authController.register(registerRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", ((com.auth.authservice.dto.ApiResponse<String>) response.getBody()).getData());
        verify(authService).register(registerRequest);
    }

    @Test
    void register_DuplicateUsername_ThrowsBadRequestException() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BadRequestException("Username already exists: john_doe"));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authController.register(registerRequest));

        assertTrue(ex.getMessage().contains("john_doe"));
    }

    @Test
    void register_DuplicateEmail_ThrowsBadRequestException() {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BadRequestException("Email already registered: john@example.com"));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authController.register(registerRequest));

        assertTrue(ex.getMessage().contains("john@example.com"));
    }

    // --- login ---

    @Test
    void login_Returns200WithTokens() {
        AuthResponse authResponse = new AuthResponse("access-token", "refresh-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(authService).login(loginRequest);
    }

    @Test
    void login_UserNotFound_ThrowsResourceNotFoundException() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found: john_doe"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authController.login(loginRequest));

        assertTrue(ex.getMessage().contains("john_doe"));
    }

    // --- logout ---

    @Test
    void logout_Returns200WithSuccessMessage() {
        LogoutRequest logoutRequest = new LogoutRequest();
        when(authService.logout(any(LogoutRequest.class))).thenReturn("Logged out successfully");

        var response = authController.logout(logoutRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Logged out successfully", ((com.auth.authservice.dto.ApiResponse<String>) response.getBody()).getData());
        verify(authService).logout(logoutRequest);
    }
}
