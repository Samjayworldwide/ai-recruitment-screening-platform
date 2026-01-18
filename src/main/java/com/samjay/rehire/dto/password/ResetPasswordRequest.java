package com.samjay.rehire.dto.password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ResetPasswordRequest {

    private String token;

    private String email;

    private String newPassword;

    private String confirmNewPassword;

}