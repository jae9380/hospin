package com.hp.hospin.member.infrastructure;

import com.hp.hospin.global.jwt.JwtTokenProvider;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JwtProviderAdapter implements JwtProvider {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String generateToken(Member member, Duration expiredAt) {
        return jwtTokenProvider.generateToken(member, expiredAt);
    }

    @Override
    public boolean isExpired(String token) {
        return jwtTokenProvider.isExpired(token);
    }

    @Override
    public String getIdentifier(String token) {
        return jwtTokenProvider.getidentifier(token);
    }
}
