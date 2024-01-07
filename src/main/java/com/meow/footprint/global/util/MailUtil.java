package com.meow.footprint.global.util;

import com.meow.footprint.global.result.error.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static com.meow.footprint.global.result.error.ErrorCode.FAIL_TO_SEND_EMAIL;

@Component
@RequiredArgsConstructor
@Log4j2
public class MailUtil {
    private final JavaMailSender emailSender;

    public void sendEmail(String toEmail,
                          String title,
                          String text) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, text);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new BusinessException(FAIL_TO_SEND_EMAIL);
        }
    }
    
    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText("<h1>인증코드 : </h1> 인증 : "+text);

        return message;
    }
    public void createMimeEmailForm(String toEmail,
                                    String title,
                                    String text) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlMsg = "<h1>인증코드 : " +text+ "</h1>";
        try {
            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setFrom("Footprint@Footprint.com");
            emailSender.send(message);
        }catch (MessagingException e){
            throw new BusinessException(FAIL_TO_SEND_EMAIL);
        }
    }
}