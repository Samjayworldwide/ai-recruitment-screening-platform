package com.samjay.rehire.service.implementation;

import com.samjay.rehire.constants.EmailVerificationType;
import com.samjay.rehire.dto.email.EmailDetails;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Async
    @Override
    public void sendVerificationEmail(String email, String token, EmailVerificationType emailVerificationType) {

        if (emailVerificationType == EmailVerificationType.REGISTRATION) {

            String message = getRegistrationEmailVerification(token);

            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, EmailDetails
                    .builder()
                    .messageBody(message)
                    .recipient(email)
                    .subject("EMAIL VERIFICATION")
                    .build()
            );

        } else if (emailVerificationType == EmailVerificationType.PASSWORD_RESET) {

            String message = getPasswordResetEmailVerification(token);

            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, EmailDetails
                    .builder()
                    .messageBody(message)
                    .recipient(email)
                    .subject("PASSWORD RESET")
                    .build()
            );

        } else if (emailVerificationType == EmailVerificationType.PASSWORD_CHANGE) {

            String message = getPasswordChangeEmailVerification(token);

            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, EmailDetails
                    .builder()
                    .messageBody(message)
                    .recipient(email)
                    .subject("PASSWORD CHANGE")
                    .build()
            );

        } else {

            throw new ApplicationException("Invalid email verification type", HttpStatus.BAD_REQUEST);

        }

    }

    private String getPasswordResetEmailVerification(String token) {

        String verificationLink = FRONTEND_URL + "verify_password_reset?token=" + token;

        return "<div style=\"font-family:Helvetica, Arial, sans-serif; font-size:16px; color:#000; background-color:#fff; padding:20px;\">" +
                "<h2 style=\"margin-bottom:20px;\">Verify Your Email Address</h2>" +
                "<p style=\"margin-bottom:20px;\">You requested to reset your password. Please click the link below to verify your email address:</p>" +
                "<p style=\"margin-bottom:20px;\"><a href=\"" + verificationLink + "\">link</a></p>" +
                "<p style=\"margin-bottom:20px;\">This verification link will expire in 10 minutes. If you did not initiate this request, please ignore this email or contact support.</p>" +
                "</div>";
    }

    private String getRegistrationEmailVerification(String token) {

        String verificationLink = FRONTEND_URL + "verify_password_change?token=" + token;

        return "<div style=\"font-family:Helvetica, Arial, sans-serif; font-size:16px; color:#000; background-color:#fff; padding:20px;\">" +
                "<h2 style=\"margin-bottom:20px;\">Verify Your Email Address</h2>" +
                "<p style=\"margin-bottom:20px;\">Thank you for registering. Please click the link below to verify your email address:</p>" +
                "<p style=\"margin-bottom:20px;\"><a href=\"" + verificationLink + "\">link</a></p>" +
                "<p style=\"margin-bottom:20px;\">This verification link will expire in 10 minutes. If you did not initiate this request, please ignore this email or contact support.</p>" +
                "</div>";
    }

    private String getPasswordChangeEmailVerification(String token) {

        String verificationLink = FRONTEND_URL + "verify_password_change?token=" + token;

        return "<div style=\"font-family:Helvetica, Arial, sans-serif; font-size:16px; color:#000; background-color:#fff; padding:20px;\">" +
                "<h2 style=\"margin-bottom:20px;\">Verify Your Email Address</h2>" +
                "<p style=\"margin-bottom:20px;\">You requested to change your password. Please click the link below to verify your email address:</p>" +
                "<p style=\"margin-bottom:20px;\"><a href=\"" + verificationLink + "\">link</a></p>" +
                "<p style=\"margin-bottom:20px;\">This verification link will expire in 10 minutes. If you did not initiate this request, please ignore this email or contact support.</p>" +
                "</div>";
    }

    @Override
    public void queueEmails(String email, String subject, String message) {

        rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, EmailDetails
                .builder()
                .messageBody(message)
                .recipient(email)
                .subject(subject)
                .build()
        );
    }
}