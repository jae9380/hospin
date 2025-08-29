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

    // etc
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String message;
}
