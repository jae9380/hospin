package com.hp.hospin.member.domain.port;

import com.hp.hospin.member.infrastructure.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByUserId(Long userId);
    void save(RefreshToken refreshToken);
}
