package com.erp.rr_erp.common.utils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SendMail {

    @Autowired
    private JavaMailSender mailSender;

    public void send(String to, int code) throws Exception {
        try {
            String template = verificationCodeTemplate(code);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Verification Code");
            helper.setText(template, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw e;
        }
    }

    private String verificationCodeTemplate(int code) {
        return """
                <!DOCTYPE html>
                <html>
                
                <body>
                    <p>Your verification code is: %d</p>
                </body>
                
                </html>""".formatted(code);
    }
}
