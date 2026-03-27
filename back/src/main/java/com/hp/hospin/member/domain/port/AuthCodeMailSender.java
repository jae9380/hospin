package com.hp.hospin.member.domain.port;

public interface AuthCodeMailSender {
    void sendAuthCodeEmail(String email, String authCode);
}
