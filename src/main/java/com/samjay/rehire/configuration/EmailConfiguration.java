package com.samjay.rehire.configuration;

import com.samjay.rehire.dto.email.EmailDetails;
import com.samjay.rehire.exception.ApplicationException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConfiguration {

    private final JavaMailSender javaMailSender;

    @Value("${email.username}")
    private String emailSender;

    @RabbitListener(queues = "email_queue")
    public void sendEmail(EmailDetails emailDetails) {

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(emailSender);

            mimeMessageHelper.setTo(emailDetails.getRecipient());

            mimeMessageHelper.setText(emailDetails.getMessageBody(), true);

            mimeMessageHelper.setSubject(emailDetails.getSubject());

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {

            throw new ApplicationException("Error sending email", HttpStatus.BAD_REQUEST);

        }
    }
}