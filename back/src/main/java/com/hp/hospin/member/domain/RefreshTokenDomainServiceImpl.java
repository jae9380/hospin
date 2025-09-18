package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.port.RefreshTokenDomainService;
import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import com.hp.hospin.member.infrastructure.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenDomainServiceImpl implements RefreshTokenDomainService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrReplace(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }
}
