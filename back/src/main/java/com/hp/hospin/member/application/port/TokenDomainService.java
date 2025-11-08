package com.hp.hospin.member.application.port;

import com.hp.hospin.member.infrastructure.entity.RefreshToken;

import java.util.Optional;

public interface TokenDomainService {
    RefreshToken saveOrReplace(Optional<RefreshToken> refreshToken, Long userId, String newRefreshToken);
}
