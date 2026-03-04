package com.hp.hospin.member.infrastructure;

import com.hp.hospin.member.domain.port.AuthCodeMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class AuthCodeMailSenderImpl implements AuthCodeMailSender {
    private static final String EMAIL_SUBJECT = "[HOSPIN] 이메일 인증을 위한 인증코드가 발급되었습니다.";
    private static final String EMAIL_TEMPLATE = "emailTemplate";

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendAuthCodeEmail(String email, String authCode) {
        Context context = new Context();
        context.setVariable("authCode", authCode);
        String htmlContent = templateEngine.process(EMAIL_TEMPLATE, context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
