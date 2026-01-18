package com.samjay.rehire.service;

import com.samjay.rehire.dto.auth.SignInRequest;
import com.samjay.rehire.dto.auth.SignInResponse;
import com.samjay.rehire.dto.auth.SignUpRequest;

public interface AuthenticationService {

    String signUpOrganization(SignUpRequest signUpRequest);

    String resendToken(String email);

    String verifyToken(String tokenValue);

    SignInResponse signInOrganization(SignInRequest signInRequest);

}