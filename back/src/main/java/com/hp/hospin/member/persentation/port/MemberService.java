package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.JoinRequest;

public interface MemberService {
    void join(JoinRequest request);
}
