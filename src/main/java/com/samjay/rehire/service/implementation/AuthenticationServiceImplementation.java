package com.samjay.rehire.service.implementation;

import com.samjay.rehire.constants.EmailVerificationType;
import com.samjay.rehire.constants.Role;
import com.samjay.rehire.dto.auth.SignInRequest;
import com.samjay.rehire.dto.auth.SignInResponse;
import com.samjay.rehire.dto.auth.SignUpRequest;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.model.EmailVerificationToken;
import com.samjay.rehire.model.Organization;
import com.samjay.rehire.repository.OrganizationRepository;
import com.samjay.rehire.security.JWTTokenGeneratorFilter;
import com.samjay.rehire.service.AuthenticationService;
import com.samjay.rehire.service.EmailService;
import com.samjay.rehire.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenGeneratorFilter jwtTokenGeneratorFilter;

    private final EmailVerificationTokenService emailVerificationTokenService;

    private final EmailService emailService;

    @Override
    public String signUpOrganization(SignUpRequest signUpRequest) {

        if (organizationRepository.existsByEmail(signUpRequest.getEmail())) {

            throw new ApplicationException("Email already registered", HttpStatus.BAD_REQUEST);

        }

        Organization organization = Organization
                .builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.ORGANIZATION)
                .build();

        organizationRepository.save(organization);

        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.createToken(organization);

        emailService.sendVerificationEmail(organization.getEmail(), emailVerificationToken.getToken(), EmailVerificationType.REGISTRATION);

        return "Registration process was successful. Please visit your email to verify your email address";

    }

    @Override
    public String resendToken(String email) {

        Organization organization = organizationRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Account not found", HttpStatus.BAD_REQUEST));

        if (organization.isVerified()) {

            throw new ApplicationException("Account has already been verified", HttpStatus.BAD_REQUEST);

        }

        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.createToken(organization);

        emailService.sendVerificationEmail(organization.getEmail(), emailVerificationToken.getToken(), EmailVerificationType.REGISTRATION);

        return "A new verification email has been sent to your email address";

    }

    @Override
    public String verifyToken(String tokenValue) {

        EmailVerificationToken emailVerificationToken = emailVerificationTokenService
                .getValidToken(tokenValue)
                .orElseThrow(() -> new ApplicationException("Invalid or expired token", HttpStatus.BAD_REQUEST));

        Organization organization = emailVerificationToken.getOrganization();

        organization.setVerified(true);

        organizationRepository.save(organization);

        emailVerificationTokenService.markTokenAsUsed(emailVerificationToken);

        return "Email address verified successfully";

    }

    @Override
    public SignInResponse signInOrganization(SignInRequest signInRequest) {

        Organization organization = organizationRepository
                .findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new ApplicationException("Invalid email or password. Please check and try again", HttpStatus.BAD_REQUEST));

        if (!passwordEncoder.matches(signInRequest.getPassword(), organization.getPassword())) {

            throw new ApplicationException("Invalid email or password. Please check and try again", HttpStatus.BAD_REQUEST);

        }

        if (!organization.isVerified()) {

            throw new ApplicationException("Your account has not been verified. Please check your email to verify your account", HttpStatus.BAD_REQUEST);

        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (signInRequest.getEmail(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtTokenGeneratorFilter.getJWT(authentication);

        return SignInResponse
                .builder()
                .token(jwtToken)
                .organizationName(organization.getName())
                .build();
    }
}