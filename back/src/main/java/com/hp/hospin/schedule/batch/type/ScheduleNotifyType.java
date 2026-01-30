package com.hp.hospin.schedule.batch.type;

public enum ScheduleNotifyType {
    BEFORE_24H(24),
    BEFORE_6H(6),
    BEFORE_3H(3),
    BEFORE_1H(1);

    private final int hours;

    ScheduleNotifyType(int hours) {
        this.hours = hours;
    }

    public int getHours() {
        return hours;
    }
}
