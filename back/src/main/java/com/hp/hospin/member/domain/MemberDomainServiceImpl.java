package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDomainServiceImpl implements MemberDomainService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register(JoinRequest request) {
        isIdentifierDuplicate(request.identifier());
        //TODO: Role 선별 기준 추가해야 함
        memberRepository.register(Member.signup(request, bCryptPasswordEncoder.encode(request.password())));
    }

    @Override
    public void login(String identifier, String password) {
        Member member = requireByIdentifier(identifier);
        verificationPassword(member.getPassword(), password);
    }

    @Override
    public Member getByIdentifier(String identifier) {
        return requireByIdentifier(identifier);
    }

    @Override
    public void existsIdentifier(String identifier) {
        isIdentifierDuplicate(identifier);
    }

    private boolean isIdentifierDuplicate(String identifier) {
        if (memberRepository.existsById(identifier)) {
            throw new MemberException.DuplicateIdentifierException();
        }
        return true;
    }
    private Member requireByIdentifier(String identifier) {
        return memberRepository.getByIdentifier(identifier)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    private void verificationPassword(String password, String input) {
        if (!bCryptPasswordEncoder.matches(input, password))
            throw new MemberException.InvalidPasswordException();
    }
}
