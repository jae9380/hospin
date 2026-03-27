package com.hp.hospin.member.application.port;

public interface MailAuthDomainService {
    void sendAuthCode(String email);
    void verifyCode(String email, String code);
}
