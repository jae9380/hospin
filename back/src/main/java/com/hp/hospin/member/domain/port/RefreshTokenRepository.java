package com.hp.hospin.member.domain.port;

import com.hp.hospin.member.infrastructure.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void save(RefreshToken refreshToken);
    void upsertByUserId(Long userId, String refreshToken);
    void deleteByUserId(Long userId);
}
