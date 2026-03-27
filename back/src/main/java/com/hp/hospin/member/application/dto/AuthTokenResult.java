package com.hp.hospin.member.application.dto;

public record AuthTokenResult(
        MemberDTO member,
        String accessToken,
        String refreshToken
) {
}
