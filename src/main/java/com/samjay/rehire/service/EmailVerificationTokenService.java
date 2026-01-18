package com.samjay.rehire.service;

import com.samjay.rehire.model.Organization;
import com.samjay.rehire.model.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenService {

    EmailVerificationToken createToken(Organization organization);

    Optional<EmailVerificationToken> getValidToken(String token);

    void markTokenAsUsed(EmailVerificationToken token);

}