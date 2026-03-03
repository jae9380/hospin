package com.hp.hospin.member.persentation.port;

import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.persentation.dto.MemberResponse;

import java.util.Map;

public interface MemberService {
    void join(MemberDTO request);
    void login(MemberDTO request);
    Map<Boolean, String> checkDuplicateIdentifier(String identifier);
    MemberDTO findByIdentifier(String name);
    Map<String, String> logoutMsg();
    String findId(String name, String email);
    String findPassword(String name, String id, String email);
}
