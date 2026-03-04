package com.hp.hospin.member.domain;

import com.hp.hospin.member.application.port.MailAuthDomainService;
import com.hp.hospin.member.domain.port.AuthCodeMailSender;
import com.hp.hospin.member.domain.port.MailAuthRedisTemplate;
import com.hp.hospin.member.exception.MemberException.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailAuthDomainServiceServiceImpl implements MailAuthDomainService {
    private final MailAuthRedisTemplate redisTemplate;
    private final AuthCodeMailSender mailSender;

    @Override
    public void sendAuthCode(String email) {
        String randomKey = generateAuthCode();
        mailSender.sendAuthCodeEmail(email, randomKey);
        redisTemplate.save(email, randomKey);
    }

    @Override
    public void verifyCode(String email, String code) {
        String authCode = redisTemplate.find(email)
                .orElseThrow(AuthCodeNotFoundException::new);

        if (!authCode.equals(code)) {
            throw new InvalidAuthCodeException();
        }

        redisTemplate.delete(email);
    }

    private String generateAuthCode() {
        return RandomStringUtils.randomAlphanumeric(5);
    }
}
