package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from.address}")
    private String fromAddress;

    @Value("${app.mail.from.name}")
    private String fromName;

    public void sendPasswordResetEmail(String toEmail, String resetToken) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, fromName);
        helper.setTo(toEmail);
        helper.setSubject("Password Reset Request");

        String htmlContent = """
            <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>You have requested to reset your password. Click the link below to proceed:</p>
                    <p>
                        <a href="http://your-frontend-url/reset-password?token=%s">
                            Reset Password
                        </a>
                    </p>
                    <p>If you didn't request this, please ignore this email.</p>
                    <p>This link will expire in 1 hour.</p>
                </body>
            </html>
        """.formatted(resetToken);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}