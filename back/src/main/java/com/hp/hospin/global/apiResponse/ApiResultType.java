package com.hp.hospin.global.apiResponse;

import lombok.Getter;

@Getter
public enum ApiResultType {
    SUCCESS,
    VALIDATION_EXCEPTION,
    IMPOSSIBLE,
    CUSTOM_EXCEPTION
}
