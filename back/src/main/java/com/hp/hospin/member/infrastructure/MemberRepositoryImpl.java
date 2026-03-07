package com.hp.hospin.member.infrastructure;

import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.infrastructure.jpa.MemberJPARepository;
import com.hp.hospin.member.infrastructure.mapper.MemberPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJPARepository memberJPARepository;
    private final MemberPersistenceMapper mapper;

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "existsById"
    )
    public boolean existsById(String id) {
        return memberJPARepository.existsByIdentifier(id);
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "register"
    )
    public void register(Member member) {
        memberJPARepository.save(mapper.domainToJpa(member));
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "getByIdentifier"
    )
    public Optional<Member> getByIdentifier(String Identifier) {
        return memberJPARepository.findJpaMemberEntityByIdentifier(Identifier)
                .map(mapper::jpaToDomain);
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "getByEmail"
    )
    public Optional<Member> getByEmail(String email) {
        return memberJPARepository.findByEmail(email)
                .map(mapper::jpaToDomain);
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "existsByEmail"
    )
    public boolean existsByEmail(String email) {
        return memberJPARepository.existsByEmail(email);
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "infrastructure",
            api = "updatePassword"
    )
    public void updatePassword(Long memberId, String encodedPassword) {
        memberJPARepository.findById(memberId)
                .ifPresent(entity -> entity.changePassword(encodedPassword));
    }
}
