package com.auth.authservice.security;

import com.auth.authservice.entity.RefreshToken;
import com.auth.authservice.entity.User;
import com.auth.authservice.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // delete old refresh token if exists
        repository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                user,
                Instant.now().plusSeconds(7 * 24 * 60 * 60) // 7 days
        );

        return repository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }
}