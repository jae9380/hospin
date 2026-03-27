package com.hp.hospin.member.domain.port;

import com.hp.hospin.member.domain.entity.Member;

import java.time.Duration;

public interface JwtProvider {
    String generateToken(Member member, Duration expiredAt);
    boolean isExpired(String token);
    String getIdentifier(String token);
}
