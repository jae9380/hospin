package com.hp.hospin.member.infrastructure.type;

public enum Role {
    ADMIN(0), USER(1), GUEST(2);
    private final int code;
    Role(int code) { this.code = code; }
    public int getCode() { return code; }
    public static Role fromCode(int code) {
        return switch(code) {
            case 0 -> ADMIN;
            case 1 -> USER;
            case 2 -> GUEST;
            default -> GUEST;
        };
    }
}