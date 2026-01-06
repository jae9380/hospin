package com.hp.hospin.member.application.port;

import com.hp.hospin.member.domain.form.JoinForm;
import com.hp.hospin.member.domain.form.LoginForm;
import com.hp.hospin.member.persentation.dto.JoinRequest;
import com.hp.hospin.member.domain.entity.Member;

public interface MemberDomainService {
    boolean existsById(String id);
    void createNewMember (JoinForm form);
    void login (LoginForm form);
    Member getByIdentifier (String identifier);
    void validatePolicy(String identifier);

}
