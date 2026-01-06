package com.hp.hospin.member.application.port;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.domain.entity.Member;

public interface MemberDomainService {
    boolean existsById(String id);
    void createNewMember (JoinRequest request);
    void login (String inputIdentifier, String inpitPassword);
    Member getByIdentifier (String identifier);
    void validatePolicy(String identifier);

}
