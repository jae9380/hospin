package com.hp.hospin.member.application;

import com.hp.hospin.member.application.dto.AuthTokenResult;
import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.application.mapper.MemberDtoMapper;
import com.hp.hospin.member.application.port.TokenDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.persentation.port.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    private final TokenDomainService tokenDomainService;
    private final MemberDtoMapper memberDtoMapper;

    @Override
    @Transactional
    public AuthTokenResult authenticateAndIssueTokens(MemberDTO memberDTO) {
        Member member = memberDtoMapper.dtoToDomain(memberDTO);

        String refreshToken = tokenDomainService.issueRefreshToken(member, REFRESH_TOKEN_DURATION);
        String accessToken = tokenDomainService.issueAccessToken(member, ACCESS_TOKEN_DURATION);

        return new AuthTokenResult(memberDTO, accessToken, refreshToken);
    }

    @Override
    public String reissueAccessToken(String refreshToken) {
        Member member = tokenDomainService.validateRefreshTokenAndGetMember(refreshToken);
        return tokenDomainService.issueAccessToken(member, ACCESS_TOKEN_DURATION);
    }

    @Override
    @Transactional
    public void deleteRefreshToken(Long userId) {
        tokenDomainService.deleteRefreshToken(userId);
    }
}
