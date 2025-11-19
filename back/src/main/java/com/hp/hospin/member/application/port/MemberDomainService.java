package com.hp.hospin.member.application.port;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.domain.entity.Member;

public interface MemberDomainService {
    Member createNewMember (JoinRequest request);
    void login (String inputIdentifier, String inpitPassword, Member target);
    Member getByIdentifier (String identifier);
    void validatePolicy(String identifier);

}
