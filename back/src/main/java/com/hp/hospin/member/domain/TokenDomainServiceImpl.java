package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.port.TokenDomainService;
import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import com.hp.hospin.member.infrastructure.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenDomainServiceImpl implements TokenDomainService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrReplace(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }
}
