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

    public static class AiResponseParseFailed extends HospinException {
        public AiResponseParseFailed() {
            super(AI_RESPONSE_PARSE_FAILED);
        }
    }

    public static class AiUnrecognizedDept extends HospinException {
        public AiUnrecognizedDept() {
            super(AI_UNRECOGNIZED_DEPT);
        }
    }

}
