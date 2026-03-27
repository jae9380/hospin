package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.AuthTokenResult;
import com.hp.hospin.member.application.dto.MemberDTO;

public interface AuthenticationService {
    AuthTokenResult authenticateAndIssueTokens(MemberDTO memberDTO);
    String reissueAccessToken(String refreshToken);
    void deleteRefreshToken(Long userId);
}
