package com.hp.hospin.member.exception;

import com.hp.hospin.global.exception.ErrorCode;
import com.hp.hospin.global.exception.HospinException;

import static com.hp.hospin.global.exception.ErrorCode.*;

public class MemberException extends HospinException {
    public MemberException(ErrorCode e) {
        super(e);
    }
    public static class DuplicateIdentifierException extends MemberException {
        public DuplicateIdentifierException() {
            super(DUPLICATE_IDENTFIER);
        }
    }

    public static class MemberNotFoundException extends MemberException {
        public MemberNotFoundException() {
            super(MEMBER_NOT_FOUND);
        }
    }

    public static class InvalidPasswordException extends MemberException {
        public InvalidPasswordException() {
            super(INVALID_PASSWORD);
        }
    }

    public static class InvalidIdentiferPolicy extends MemberException {
        public InvalidIdentiferPolicy() {
            super(INVALID_IDENTIFIER_POLICY);
        }
    }
}
