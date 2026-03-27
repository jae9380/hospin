package com.hp.hospin.member.domain;

import com.hp.hospin.global.exception.ErrorCode;
import com.hp.hospin.global.exception.HospinException;
import com.hp.hospin.member.application.port.TokenDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.JwtProvider;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.hp.hospin.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class TokenDomainServiceImpl implements TokenDomainService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public String issueRefreshToken(Member member, Duration duration) {
        String newRefreshToken = jwtProvider.generateToken(member, duration);
        refreshTokenRepository.upsertByUserId(member.getId(), newRefreshToken);
        return newRefreshToken;
    }

    @Override
    public String issueAccessToken(Member member, Duration duration) {
        return jwtProvider.generateToken(member, duration);
    }

    @Override
    @Transactional(readOnly = true)
    public Member validateRefreshTokenAndGetMember(String refreshToken) {
        validateTokenNotExpired(refreshToken);

        refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new HospinException(INVALID_REFRESH_TOKEN));

        String identifier = jwtProvider.getIdentifier(refreshToken);

        return memberRepository.getByIdentifier(identifier)
                .orElseThrow(() -> new HospinException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private void validateTokenNotExpired(String token) {
        try {
            if (jwtProvider.isExpired(token)) {
                throw new HospinException(INVALID_REFRESH_TOKEN);
            }
        } catch (ExpiredJwtException e) {
            throw new HospinException(INVALID_REFRESH_TOKEN);
        }
    }
}
