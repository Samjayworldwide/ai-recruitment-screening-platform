package com.samjay.rehire.service.implementation;

import com.samjay.rehire.model.EmailVerificationToken;
import com.samjay.rehire.model.Organization;
import com.samjay.rehire.repository.EmailVerificationTokenRepository;
import com.samjay.rehire.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImplementation implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    public EmailVerificationToken createToken(Organization organization) {

        return emailVerificationTokenRepository.findByOrganization(organization)

                .map(token -> {

                    token.setToken(UUID.randomUUID().toString());

                    token.setExpiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 10));

                    token.setUsed(false);

                    return emailVerificationTokenRepository.save(token);

                })
                .orElseGet(() -> {

                    EmailVerificationToken newToken = new EmailVerificationToken();

                    newToken.setOrganization(organization);

                    newToken.setToken(UUID.randomUUID().toString());

                    newToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 10));

                    newToken.setUsed(false);

                    return emailVerificationTokenRepository.save(newToken);

                });
    }

    @Override
    public Optional<EmailVerificationToken> getValidToken(String token) {

        return emailVerificationTokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiryDate().after(new Date()));
    }

    @Override
    public void markTokenAsUsed(EmailVerificationToken token) {

        token.setUsed(true);

        emailVerificationTokenRepository.save(token);

    }
}