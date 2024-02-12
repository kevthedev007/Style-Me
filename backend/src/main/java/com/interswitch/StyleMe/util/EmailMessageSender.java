package com.interswitch.StyleMe.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailMessageSender {
    private final JavaMailSender mailSender;
    private final String serverEmail;

    public EmailMessageSender(
            final JavaMailSender mailSender,
            @Value("${app.confirmation.sender.email}")
            final String serverEmail
    ) {
        this.mailSender = mailSender;
        this.serverEmail = serverEmail;
    }

    public void sendMessage(String to, String subject, String text) {
        final MimeMessage message = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(serverEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
    }

    public void sendVerificationCode(String email , int code) {
        sendMessage(email, "Email Verification", String.format("Verification Code: %s", code));
    }
}
