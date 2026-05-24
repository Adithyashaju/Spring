package com.auth.authservice.security;

import com.auth.authservice.entity.RefreshToken;
import com.auth.authservice.entity.User;
import com.auth.authservice.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final User user = new User("john_doe", "john@example.com", "encoded", "USER", true);

    @Test
    void createRefreshToken_DeletesOldAndSavesNew() {
        RefreshToken saved = new RefreshToken("new-token", user, Instant.now().plusSeconds(3600));
        doNothing().when(repository).deleteByUserId(user.getId());
        when(repository.save(any(RefreshToken.class))).thenReturn(saved);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals("new-token", result.getToken());
        verify(repository).deleteByUserId(user.getId());
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ValidToken_ReturnsToken() {
        RefreshToken token = new RefreshToken("valid-token", user, Instant.now().plusSeconds(3600));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
        verify(repository, never()).delete(any());
    }

    @Test
    void verifyExpiration_ExpiredToken_ThrowsRuntimeException() {
        RefreshToken expired = new RefreshToken("expired-token", user, Instant.now().minusSeconds(1));
        doNothing().when(repository).delete(expired);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> refreshTokenService.verifyExpiration(expired));

        assertEquals("Refresh token expired", ex.getMessage());
        verify(repository).delete(expired);
    }
}
