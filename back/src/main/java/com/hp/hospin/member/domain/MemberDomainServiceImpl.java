package com.hp.hospin.member.domain;

import com.hp.hospin.member.domain.form.JoinForm;
import com.hp.hospin.member.domain.form.LoginForm;
import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.exception.MemberException.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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
    public boolean existsById(String id) {
        return memberRepository.existsById(id);
    }

    @Override
    public void createNewMember(JoinForm form) throws DuplicateIdentifierException{
        // TODO: Role 선별 기준 추가해야 함
        // Note: 회원가입 내 도메인 로직 생각하기
        Member member = Member.signup(form, bCryptPasswordEncoder.encode(form.getPassword()));

        memberRepository.register(member);
    }

    @Override
    public void login(LoginForm form) throws InvalidPasswordException {
        Member target = requireByIdentifier(form.getIdentifier());

        verificationPassword(target.getPassword(), form.getPassword());
    }

    @Override
    public Member getByIdentifier(String identifier) {
//        Note: CustomUserDetailsService을 위해 작성
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

    @Override
    public String findIdentifierByEmail(String name, String email) {
        Member member = memberRepository.getByEmail(email).orElseThrow(MemberNotFoundException::new);

        member.verifyName(name);

        return member.getIdentifier();
    }

    @Override
    public void verifyMemberInfoByEmail(String email, String id, String name) {
        Member member = memberRepository.getByEmail(email).orElseThrow(MemberNotFoundException::new);
        member.verifyIdentifier(id);
        member.verifyName(name);
    }

    @Override
    public void existsByEmail(String email) {
        if(memberRepository.existsByEmail(email)) throw new DuplicateEmailException();
    }

    @Override
    public void resetPassword(String email, String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new PasswordMismatchException();
        }
        Member member = memberRepository.getByEmail(email).orElseThrow(MemberNotFoundException::new);
        memberRepository.updatePassword(member.getId(), bCryptPasswordEncoder.encode(newPassword));
    }
    private Member requireByIdentifier(String identifier) {
        return memberRepository.getByIdentifier(identifier)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void verificationPassword(String target, String input) {
        if (!bCryptPasswordEncoder.matches(input, target))
            throw new InvalidPasswordException();
    }
}
