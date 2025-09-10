package com.hp.hospin.global.exception;

import lombok.Getter;

@Getter
public class HospinException extends RuntimeException{
    private final ErrorCode errorCode;

    public HospinException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
