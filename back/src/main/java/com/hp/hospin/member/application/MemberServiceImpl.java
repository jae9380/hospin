package com.hp.hospin.member.application;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.persentation.dto.LoginRequest;
import com.hp.hospin.member.persentation.port.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// TODO: 전체적인 구조 수정 필요
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberDomainService memberDomainService;
    private final MemberRepository memberRepository;

    @Override
    public void join(JoinRequest request) {
        memberDomainService.register(request);
    }

    @Override
    public void login(LoginRequest request) {
        memberDomainService.login(request.identifier(), request.password());
    }

    @Override
    public void checkDuplicateIdentifier(String identifier) {
        memberDomainService.existsIdentifier(identifier);
    }

    @Override
    public Member findByIdentifier(String identifier) {
        // TODO: 해당 유저가 없을 경우에 대한 예외 설정 필요
        return memberRepository.getByIdentifier(identifier).get();
    }
}
