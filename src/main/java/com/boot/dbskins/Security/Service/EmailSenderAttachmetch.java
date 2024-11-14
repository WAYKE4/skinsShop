package com.boot.dbskins.Security.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAttachmetch {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailSenderAttachmetch(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMessageWithAttachment(
            String to, String subject, String activationLink) {

        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("maximdrabysheuski@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("<html><body>" +
                    "<p>Пожалуйста, нажмите кнопку ниже, чтобы активировать свой аккаунт:</p>" +
                    "<p><a href='" + activationLink + "'>" +
                    "<button style='background-color: #4CAF50; color: #ffffff; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer;'>Активировать аккаунт</button>" +
                    "</a></p>" +
                    "</body></html>", true);
            emailSender.send(message);
        } catch (MessagingException e) {
            System.out.println(e);
        }
    }
}
