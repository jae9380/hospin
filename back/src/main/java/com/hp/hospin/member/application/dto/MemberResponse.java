package com.hp.hospin.member.application.dto;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String identifier,
        String name,
        String phoneNumber,
        String email,
        int gender,
        LocalDate birth
) {
}