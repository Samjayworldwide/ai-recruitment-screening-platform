package com.samjay.rehire.service;

import com.samjay.rehire.dto.password.ChangePasswordRequest;
import com.samjay.rehire.dto.password.ResetPasswordRequest;

public interface PasswordService {

    String requestPasswordReset(String email);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    String changePassword(ChangePasswordRequest changePasswordRequest);

}