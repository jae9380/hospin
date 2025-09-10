package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDomainServiceImpl implements MemberDomainService {
    private final MemberRepository memberRepository;

    @Override
    public void register(JoinRequest request) {
        if (memberRepository.existsById(request.identifier())) {
            //TODO: custom exception 발생
            //TODO: Policy 이동
        }
        //TODO: Role 선별 기준 추가해야 함
        memberRepository.register(Member.signup(request));
    }
}
