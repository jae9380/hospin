package com.hp.hospin.member.application;

import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.application.mapper.MemberDtoMapper;
import com.hp.hospin.member.application.port.MailAuthDomainService;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.exception.MemberException.*;
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
    private final MailAuthDomainService mailAuthDomainService;
    private final MemberDtoMapper mapper;

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "join"
    )
    public void join(MemberDTO request) {
        if (memberDomainService.existsById(request.getIdentifier())) throw new DuplicateIdentifierException();

        memberDomainService.createNewMember(mapper.dtoToJoinForm(request));
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "login"
    )
    public void login(MemberDTO request) {
        memberDomainService.login(mapper.dtoToLoginForm(request));
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "checkDuplicateIdentifier"
    )
    public Map<Boolean, String> checkDuplicateIdentifier(String identifier) {
        Map<Boolean, String> resultMap = new HashMap<>();

        try {
            memberDomainService.validatePolicy(identifier);

            boolean exists = memberDomainService.existsById(identifier);
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
    @Monitored(
            domain = "member",
            layer = "application",
            api = "findByIdentifier"
    )
    public MemberDTO findByIdentifier(String identifier) {
        // TODO: 해당 유저가 없을 경우에 대한 예외 설정 필요
        return mapper.domainToDto(memberDomainService.getByIdentifier(identifier));
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "logoutMsg"
    )
    public Map<String, String> logoutMsg() {
        Map<String, String> logoutMsg = new HashMap<>();
        logoutMsg.put("message", "로그아웃 되었습니다.");

        return logoutMsg;
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "findId"
    )
    public String findId(String name, String email) {
        String identifier = memberDomainService.findIdentifierByEmail(name, email);

        return identifier;
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "verifyAndSendAuthCode"
    )
    public String verifyAndSendAuthCode(String name, String id, String email) {
        memberDomainService.verifyMemberInfoByEmail(email, id, name);
        mailAuthDomainService.sendAuthCode(email);
        return null;
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "sendAuthEmail"
    )
    public void sendAuthEmail(String email) {
        memberDomainService.existsByEmail(email);
        mailAuthDomainService.sendAuthCode(email);
    }

    @Override
    @Monitored(
            domain = "member",
            layer = "application",
            api = "verifyCode"
    )
    public void verifyCode(String email, String code) {
        mailAuthDomainService.verifyCode(email, code);
    }

    @Override
    @Transactional
    @Monitored(
            domain = "member",
            layer = "application",
            api = "resetPassword"
    )
    public void resetPassword(String email, String newPassword, String confirmNewPassword) {
        memberDomainService.resetPassword(email, newPassword, confirmNewPassword);
    }
}
