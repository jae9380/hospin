package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.exception.MemberException.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDomainServiceImpl implements MemberDomainService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    static final List<String> BANNED_WORDS = List.of("admin", "root", "master");

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
    public void validatePolicy(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new InvalidIdentiferPolicy();
        }

        if (identifier.contains(" ")) {
            throw new InvalidIdentiferPolicy();
        }

        if (identifier.length() < 6 || identifier.length() > 20) {
            throw new InvalidIdentiferPolicy();
        }

        if (!identifier.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidIdentiferPolicy();
        }

        if (Character.isDigit(identifier.charAt(0))) {
            throw new InvalidIdentiferPolicy();
        }

        if (identifier.matches("(.)\\1{5,}")) {
            throw new InvalidIdentiferPolicy();
        }

        for (String word : BANNED_WORDS) {
            if (identifier.toLowerCase().contains(word)) {
                throw new InvalidIdentiferPolicy();
            }
        }
    }

    private boolean isIdentifierDuplicate(String identifier) {
        if (memberRepository.existsById(identifier)) {
            throw new DuplicateIdentifierException();
        }
        return true;
    }
    private Member requireByIdentifier(String identifier) {
        return memberRepository.getByIdentifier(identifier)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void verificationPassword(String password, String input) {
        if (!bCryptPasswordEncoder.matches(input, password))
            throw new InvalidPasswordException();
    }
}
