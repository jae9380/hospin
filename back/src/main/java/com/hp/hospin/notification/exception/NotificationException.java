package com.hp.hospin.notification.exception;

import com.hp.hospin.global.exception.ErrorCode;
import com.hp.hospin.global.exception.HospinException;
import com.hp.hospin.schedule.exception.ScheduleException;

import static com.hp.hospin.global.exception.ErrorCode.FCM_NOT_EXIST;

public class NotificationException extends HospinException {
    public NotificationException(ErrorCode e) {
        super(e);
    }

    public static class FCMNotExistException extends NotificationException {
        public FCMNotExistException() {
            super(FCM_NOT_EXIST);
        }
    }

}
