package com.samjay.rehire.service.implementation;

import com.samjay.rehire.constants.EmailVerificationType;
import com.samjay.rehire.dto.password.ChangePasswordRequest;
import com.samjay.rehire.dto.password.ResetPasswordRequest;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.model.EmailVerificationToken;
import com.samjay.rehire.model.Organization;
import com.samjay.rehire.repository.OrganizationRepository;
import com.samjay.rehire.service.EmailService;
import com.samjay.rehire.service.EmailVerificationTokenService;
import com.samjay.rehire.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class PasswordServiceImplementation implements PasswordService {

    private final EmailVerificationTokenService emailVerificationTokenService;

    private final OrganizationRepository organizationRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String requestPasswordReset(String email) {

        Organization organization = returnOrganizationByEmail(email);

        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.createToken(organization);

        emailService.sendVerificationEmail(email, emailVerificationToken.getToken(), EmailVerificationType.PASSWORD_RESET);

        return "A verification email has been sent to your email address for password reset";

    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {

        Organization organization = returnOrganizationByEmail(resetPasswordRequest.getEmail());

        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())) {

            throw new ApplicationException("Confirm password and password do not match please check and try again", BAD_REQUEST);

        }

        organization.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        organizationRepository.save(organization);

        return "Password was reset successfully";

    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Organization organization = returnOrganizationByEmail(email);

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), organization.getPassword())) {

            throw new ApplicationException("Old password and current password do not match. please check and try again", BAD_REQUEST);

        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {

            throw new ApplicationException("New password and confirm password do not match please check and try again", BAD_REQUEST);

        }

        organization.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        organizationRepository.save(organization);

        return "Password was changed successfully";

    }

    private Organization returnOrganizationByEmail(String email) {

        return organizationRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Organization not found", HttpStatus.BAD_REQUEST));
    }
}