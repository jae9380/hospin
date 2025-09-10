package com.hp.hospin.member.application.port;

import com.hp.hospin.member.application.dto.JoinRequest;

public interface MemberDomainService {
    void register(JoinRequest request);
}
