package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.persentation.dto.LoginRequest;

public interface MemberService {
    void join(JoinRequest request);
    void login(LoginRequest request);
}
