package com.hp.hospin.member.infrastructure.type;

public enum Gender {
    MALE(0), FEMALE(1), UNDEFINED(2);
    private final int code;
    Gender(int code) {this.code = code;}
    public int getCode() { return code; }
    public static Gender fromCode(int code) {
        return switch(code) {
            case 0 -> MALE;
            case 1 -> FEMALE;
            case 2 -> UNDEFINED;
            default -> UNDEFINED;
        };
    }
}