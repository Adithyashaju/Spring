package com.auth.authservice.service;

import com.auth.authservice.dto.AuthResponse;
import com.auth.authservice.dto.LoginRequest;
import com.auth.authservice.dto.LogoutRequest;
import com.auth.authservice.dto.RegisterRequest;
import com.auth.authservice.entity.User;
import com.auth.authservice.exception.BadRequestException;
import com.auth.authservice.exception.ResourceNotFoundException;
import com.auth.authservice.repository.RefreshTokenRepository;
import com.auth.authservice.repository.UserRepository;
import com.auth.authservice.security.JwtUtil;
import com.auth.authservice.security.RefreshTokenService;
import com.auth.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

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
    void register_Success() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");

        String result = authService.register(registerRequest);

        assertEquals("USER registered successfully", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsBadRequestException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("john_doe"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_DuplicateEmail_ThrowsBadRequestException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("john@example.com"));
        verify(userRepository, never()).save(any());
    }

    // --- login ---

    @Test
    void login_Success() {
        User user = new User("john_doe", "john@example.com", "encoded", "USER", true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("john_doe", "USER")).thenReturn("access-token");

        com.auth.authservice.entity.RefreshToken refreshToken =
                new com.auth.authservice.entity.RefreshToken("refresh-token", user, java.time.Instant.now().plusSeconds(3600));
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response = authService.login(loginRequest);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_UserNotFound_ThrowsResourceNotFoundException() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authService.login(loginRequest));

        assertTrue(ex.getMessage().contains("john_doe"));
    }

    // --- logout ---

    @Test
    void logout_Success() {
        LogoutRequest logoutRequest = new LogoutRequest();

        String result = authService.logout(logoutRequest);

        assertEquals("Logged out successfully", result);
        verify(refreshTokenRepository).findByToken(any());
    }
}
