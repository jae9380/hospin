package com.hp.hospin.member.domain.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class MemberDetails implements UserDetails {
    private final Member member;

    @Override
    public String getUsername() {
        return member.getIdentifier();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    public Long getId() {
        return member.getId();
    }

    public int getGender() {
        return member.getGender();
    }

    public int getRole() {
        return member.getRole();
    }

    public LocalDate getBirth() {
        return member.getBirth();
    }

    public String getName() {
        return member.getName();
    }

    public String getPhoneNumber() {
        return member.getPhoneNumber();
    }

    public String getEmail() {
        return member.getEmail();
    }

    // OAuth2User Override
    public Map<String, Object> getAttributes() {
        return getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singletonList(() -> member.getRole().name());
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {   // 계정이 만료되지 않은 상태인지
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {   // 계정이 잠겨있지 않은 상태인지
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {   // 인증 정보가 만료되지 않은 상태인지
        return true;
    }

    @Override
    public boolean isEnabled() {    // 계정이 활성화된 상태인지
        return true;
    }
}