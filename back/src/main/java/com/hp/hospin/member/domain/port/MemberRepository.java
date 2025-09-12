package com.hp.hospin.member.domain.port;

import com.hp.hospin.member.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository {
    boolean existsById(String id);
    void register(Member signup);
    Optional<Member> getByIdentifier(String Identifier);
}
