package com.hp.hospin.member.application.port;

import com.hp.hospin.member.domain.entity.Member;

import java.time.Duration;

public interface TokenDomainService {
    String issueRefreshToken(Member member, Duration duration);
    String issueAccessToken(Member member, Duration duration);
    Member validateRefreshTokenAndGetMember(String refreshToken);
    void deleteRefreshToken(Long userId);
}
