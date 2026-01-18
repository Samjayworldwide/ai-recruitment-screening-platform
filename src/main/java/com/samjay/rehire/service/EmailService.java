package com.samjay.rehire.service;

import com.samjay.rehire.constants.EmailVerificationType;

public interface EmailService {

    void sendVerificationEmail(String email, String token, EmailVerificationType emailVerificationType);

    void queueEmails(String email, String subject, String message);

}