package com.hp.hospin.global.exception;

import static com.hp.hospin.global.exception.ErrorCode.*;

public class HospinCustomException extends HospinException {
    public HospinCustomException(final ErrorCode e) {
        super(e);
    }

    public static class HospitalNotExist extends HospinException {
        public HospitalNotExist() {
            super(HOSPITAL_NOT_EXIST);
        }
    }
}
