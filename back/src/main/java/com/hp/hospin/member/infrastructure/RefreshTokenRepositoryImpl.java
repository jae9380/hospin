package com.hp.hospin.member.infrastructure;

import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import com.hp.hospin.member.infrastructure.entity.RefreshToken;
import com.hp.hospin.member.infrastructure.jpa.RefreshTokenJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJPARepository refreshTokenJPARepository;
    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenJPARepository.findByUserId(userId);
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenJPARepository.save(refreshToken);
    }
}
