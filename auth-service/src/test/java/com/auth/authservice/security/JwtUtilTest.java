package com.auth.authservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("testsecretkeytestsecretkey12345678");
    }

    @Test
    void generateToken_WithRole_ReturnsNonNullToken() {
        String token = jwtUtil.generateToken("john_doe", "USER");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_WithoutRole_ReturnsNonNullToken() {
        String token = jwtUtil.generateToken("john_doe");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        String token = jwtUtil.generateToken("john_doe", "USER");

        assertEquals("john_doe", jwtUtil.extractUsername(token));
    }

    @Test
    void extractRole_ReturnsCorrectRole() {
        String token = jwtUtil.generateToken("john_doe", "ADMIN");

        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("john_doe", "USER");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.value"));
    }

    @Test
    void validateToken_TamperedToken_ReturnsFalse() {
        String token = jwtUtil.generateToken("john_doe", "USER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(jwtUtil.validateToken(tampered));
    }
}
