package com.hp.hospin.member.application;

import com.hp.hospin.global.jwt.CookieUtil;
import com.hp.hospin.global.jwt.JwtTokenProvider;
import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.application.mapper.MemberAppMapper;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.application.port.TokenDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.domain.port.RefreshTokenRepository;
import com.hp.hospin.member.persentation.port.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDomainService memberDomainService;
    private final TokenDomainService refreshTokenDomainService;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberAppMapper mapper;

    public MemberResponse authenticateAndSetTokens(String identifier, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberDomainService.getByIdentifier(identifier);

        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        refreshTokenDomainService.saveOrReplace(member.getId(), refreshToken);
        addTokenToCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_DURATION);

        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addTokenToCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, ACCESS_TOKEN_DURATION);

        return mapper.domainToDto(member);
    }

    private void addTokenToCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String token, Duration duration) {
        int cookieMaxAge = (int) duration.toSeconds();
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, token, cookieMaxAge);
    }
}

