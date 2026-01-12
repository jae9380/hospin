package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    MemberDTO authenticateAndSetTokens(String identifier, HttpServletRequest request, HttpServletResponse response);
}
