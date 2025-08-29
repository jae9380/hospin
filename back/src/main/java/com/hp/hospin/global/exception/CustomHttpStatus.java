package com.hp.hospin.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomHttpStatus {
    private int statusCode;
    private String statusMessage;
}
