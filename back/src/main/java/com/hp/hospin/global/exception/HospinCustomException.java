package com.hp.hospin.global.exception;

import static com.hp.hospin.global.exception.ErrorCode.*;

public class HospinCustomException extends HospinException {
    public HospinCustomException(final ErrorCode e) {
        super(e);
    }

    public static class NotFoundFileException extends HospinCustomException {
        public NotFoundFileException() {
            super(NOT_FOUND_FILE);
        }
    }
}
