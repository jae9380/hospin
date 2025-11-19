package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.port.TokenDomainService;
import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import com.hp.hospin.member.infrastructure.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenDomainServiceImpl implements TokenDomainService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken saveOrReplace(Optional<RefreshToken> refreshToken, Long userId, String newRefreshToken) {
        return refreshToken.map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));
    }
}
