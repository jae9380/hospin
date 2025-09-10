package com.hp.hospin.member.application;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.persentation.port.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberDomainService memberDomainService;

    @Override
    public void join(JoinRequest request) {
        memberDomainService.register(request);
    }
}
