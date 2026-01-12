package com.hp.hospin.schedule.infrastructure.entity.type;

public enum ScheduleType {
    ONE_TIME(0), RECURRING(1);

    private final int code;
    ScheduleType(int code) {this.code = code;}
    public int getCode() { return code; }
    public static ScheduleType fromCode(int code) {
        return switch(code) {
            case 1 -> ONE_TIME;
            case 2 -> RECURRING;
            default -> ONE_TIME;
        };
    }
}
