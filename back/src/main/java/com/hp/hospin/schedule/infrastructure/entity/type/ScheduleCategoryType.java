package com.hp.hospin.schedule.infrastructure.entity.type;

public enum ScheduleCategoryType {
    SURGERY(1),     // 수술
    CONSULTATION(2), // 진료
    TREATMENT(3),    // 치료
    MEETING(4),      // 병원 회의 등
    OTHER(0);         // 기타

    private final int code;
    ScheduleCategoryType(int code) {this.code = code;}
    public int getCode() { return code; }
    public static ScheduleCategoryType fromCode(int code) {
        return switch(code) {
            case 1 -> SURGERY;
            case 2 -> CONSULTATION;
            case 3 -> TREATMENT;
            case 4 -> MEETING;
            default -> OTHER;
        };
    }
}