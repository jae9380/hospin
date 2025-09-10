package com.hp.hospin.member.domain.entity;

import com.hp.hospin.member.application.dto.JoinRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class Member {
    private final Long id;
    private final String identifier;
    private final String password;
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final int role;
    private final int gender;
    private final LocalDate birth;

    public Member(Long id, String identifier, String password, String name, String phoneNumber,
                  String email, int role, int gender, LocalDate birth) {
        this.id = id;
        this.identifier = identifier;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birth = birth;
    }

    public static Member signup(JoinRequest request) {
        return Member.builder()
                .id(null)
                .identifier(request.identifier())
                .password(request.password())
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .role(2)
                .gender(request.gender())
                .birth(request.birth())
                .build();
    }
}
