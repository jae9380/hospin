package com.hp.hospin.member.application.auth;

import com.hp.hospin.member.application.port.MemberDomainService;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.global.common.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberDomainService memberDomainService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Member member = memberDomainService.getByIdentifier(identifier);

        return new MemberDetails(member);
    }
}
