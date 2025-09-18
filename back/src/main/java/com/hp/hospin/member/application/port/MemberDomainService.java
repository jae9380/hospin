package com.hp.hospin.member.application.port;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.domain.entity.Member;

public interface MemberDomainService {
    void register (JoinRequest request);
    void login (String identifier, String password);
    Member getByIdentifier (String identifier);
    void existsIdentifier(String identifier);
}
