package com.hp.hospin.member.infrastructure;

import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.infrastructure.jpa.MemberJPARepository;
import com.hp.hospin.member.infrastructure.mapper.MemberPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJPARepository memberJPARepository;
    private final MemberPersistenceMapper mapper;

    @Override
    public boolean existsById(String id) {
        return memberJPARepository.existsByIdentifier(id);
    }

    @Override
    public void register(Member member) {
        memberJPARepository.save(mapper.domainToJpa(member));
    }
}
