package com.hp.hospin.member.application.dto;

import java.time.LocalDate;

public record MemberResponse(
        // TODO: 너무 많은 내용을 반환하고 있다. 수정 필요
        Long id,
        String identifier,
        String name,
        String phoneNumber,
        String email,
        int gender,
        LocalDate birth
) {
}