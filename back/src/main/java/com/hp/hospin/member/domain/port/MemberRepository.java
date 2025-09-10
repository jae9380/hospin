package com.hp.hospin.member.domain.port;

import com.hp.hospin.member.domain.entity.Member;

public interface MemberRepository {
    boolean existsById(String id);

    void register(Member signup);
}
