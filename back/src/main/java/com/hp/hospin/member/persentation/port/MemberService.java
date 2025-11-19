package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.application.dto.LoginRequest;

import java.util.Map;

public interface MemberService {
    void join(JoinRequest request);
    void login(LoginRequest request);
    Map<Boolean, String> checkDuplicateIdentifier(String identifier);
    MemberResponse findByIdentifier(String name);
    Map<String, String> logoutMsg();
}
