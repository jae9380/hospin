package com.hp.hospin.schedule.exception;

import com.hp.hospin.global.exception.ErrorCode;
import com.hp.hospin.global.exception.HospinException;

import static com.hp.hospin.global.exception.ErrorCode.*;

public class ScheduleException extends HospinException {
    public ScheduleException(ErrorCode e) {
        super(e);
    }
    public static class ScheduleNotExistException extends ScheduleException {
        public ScheduleNotExistException() {
            super(SCHEDULE_NOT_EXIST);
        }
    }

    public static class NoPerissionException extends ScheduleException {
        public NoPerissionException() {
            super(NO_PERMISSION);
        }
    }

}
