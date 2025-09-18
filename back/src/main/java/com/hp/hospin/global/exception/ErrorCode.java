package com.hp.hospin.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Hospital
    HOSPITAL_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 병원은 존재하지 않습니다."),

    // File
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_READING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 읽는 중 오류가 발생했습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "파일 형식이 올바르지 않습니다."),

    // member
    DUPLICATE_IDENTFIER(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원정보가 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // etc
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String message;
}
