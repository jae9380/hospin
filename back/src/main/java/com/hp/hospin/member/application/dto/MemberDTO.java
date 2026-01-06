package com.hp.hospin.member.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberDTO {
    private final Long id;
    private final String identifier;
    private final String password;
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final int role;
    private final int gender;
    private final LocalDate birth;
}
