package com.hp.hospin.schedule.infrastructure.entity.type;

public enum RecurringType {
    NONE(0), DAILY(1), WEEKLY(2), MONTHLY(3);
    private final int code;
    RecurringType(int code) {this.code = code;}
    public int getCode() { return code; }
    public static RecurringType fromCode(int code) {
        return switch(code) {
            case 0 -> NONE;
            case 1 -> DAILY;
            case 2 -> WEEKLY;
            case 3 -> MONTHLY;
            default -> NONE;
        };
    }
}
