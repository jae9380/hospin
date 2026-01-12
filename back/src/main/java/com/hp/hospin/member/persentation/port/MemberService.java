package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.MemberDTO;

import java.util.Map;

public interface MemberService {
    void join(MemberDTO request);
    void login(MemberDTO request);
    Map<Boolean, String> checkDuplicateIdentifier(String identifier);
    MemberDTO findByIdentifier(String name);
    Map<String, String> logoutMsg();
}
