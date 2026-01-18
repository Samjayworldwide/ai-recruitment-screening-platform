package com.samjay.rehire.controller;

import com.samjay.rehire.dto.api.ApiResponse;
import com.samjay.rehire.dto.auth.SignInRequest;
import com.samjay.rehire.dto.auth.SignInResponse;
import com.samjay.rehire.dto.auth.SignUpRequest;
import com.samjay.rehire.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/signup")
    public ResponseEntity<ApiResponse<String>> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(authenticationService.signUpOrganization(signUpRequest)));

    }

    @PostMapping(value = "/resend-token")
    public ResponseEntity<ApiResponse<String>> resendVerificationToken(@RequestParam String email) {

        return ResponseEntity.ok(new ApiResponse<>(authenticationService.resendToken(email)));

    }

    @PostMapping(value = "/verify-token")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestParam String token) {

        return ResponseEntity.ok(new ApiResponse<>(authenticationService.verifyToken(token)));

    }

    @PostMapping(value = "/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest signInRequest) {

        return ResponseEntity.ok(new ApiResponse<>(authenticationService.signInOrganization(signInRequest)));

    }
}