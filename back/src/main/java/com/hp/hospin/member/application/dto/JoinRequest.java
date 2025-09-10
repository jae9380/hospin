package com.hp.hospin.member.application.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record JoinRequest(
        @NotBlank(message = "아이디는 필수 항목입니다.")
        @Size(min = 4, max = 20, message = "아이디는 최소 4자 ~ 최대 20자 이내로 입력해주세요.")
        String identifier,

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 ~ 최대 20자 이내로 입력해주세요.")
        String password,

        @NotBlank(message = "이름은 필수 항목입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "이름은 한글 또는 영문만 가능합니다.")
        String name,

        @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
        @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰 번호는 숫자만 10자리 또는 11자리여야 합니다.")
        String phoneNumber,

        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        String email,

        int gender,

        @NotNull(message = "생년월일은 필수 입력 항목입니다.")
        @Past(message = "생년월일은 현재보다 앞선 날짜여야 합니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth
) {}
