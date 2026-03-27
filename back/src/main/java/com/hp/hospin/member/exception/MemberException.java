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

    public static class DuplicateEmailException extends MemberException {
        public DuplicateEmailException() {
            super(DUPLICATE_EMAIL);
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

    public static class InvalidMemberInfoException extends MemberException {
        public InvalidMemberInfoException() {
            super(INVALID_MEMBER_INFO);
        }
    }

    public static class InvalidAuthCodeException extends MemberException {
        public InvalidAuthCodeException() {
            super(INVALID_AUTH_CODE);
        }
    }

    public static class AuthCodeNotFoundException extends MemberException {
        public AuthCodeNotFoundException() {
            super(AUTH_CODE_NOT_FOUND);
        }
    }

    public static class PasswordMismatchException extends MemberException {
        public PasswordMismatchException() {
            super(PASSWORD_MISMATCH);
        }
    }
}
