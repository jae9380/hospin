package com.hp.hospin.member.application;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.application.mapper.MemberDtoMapper;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.exception.MemberException.*;
import com.hp.hospin.member.application.dto.LoginRequest;
import com.hp.hospin.member.persentation.port.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

// TODO: 전체적인 구조 수정 필요
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberDomainService memberDomainService;
    private final MemberRepository memberRepository;
    private final MemberDtoMapper mapper;

    @Override
    public void join(JoinRequest request) {

        if (memberRepository.existsById(request.identifier()))
            throw new DuplicateIdentifierException();

        Member newMember = memberDomainService.createNewMember(request);

        memberRepository.register(newMember);
    }

    @Override
    public void login(LoginRequest request) {
        Member member = memberRepository.getByIdentifier(request.identifier())
                .orElseThrow(MemberNotFoundException::new);

        memberDomainService.login(request.identifier(), request.password(), member);
    }

    @Override
    public Map<Boolean, String> checkDuplicateIdentifier(String identifier) {
        Map<Boolean, String> resultMap = new HashMap<>();

        try {
            memberDomainService.validatePolicy(identifier);

            boolean exists = memberRepository.existsById(identifier);
            if (exists) {
                resultMap.put(false, "이미 존재하는 아이디입니다.");
            } else {
                resultMap.put(true, "사용 가능한 아이디입니다.");
            }

        } catch (InvalidIdentiferPolicy e) {
            resultMap.put(false, e.getMessage());
        }

        return resultMap;
    }

    @Override
    public MemberResponse findByIdentifier(String identifier) {
        // TODO: 해당 유저가 없을 경우에 대한 예외 설정 필요
        return mapper.domainToResponse(memberRepository.getByIdentifier(identifier).get());
    }

    @Override
    public Map<String, String> logoutMsg() {
        Map<String, String> logoutMsg = new HashMap<>();
        logoutMsg.put("message", "로그아웃 되었습니다.");

        return logoutMsg;
    }
}
