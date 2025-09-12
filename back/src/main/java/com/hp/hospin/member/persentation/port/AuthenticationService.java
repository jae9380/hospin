package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    MemberResponse authenticateAndSetTokens(String identifier, HttpServletRequest request, HttpServletResponse response);
}
