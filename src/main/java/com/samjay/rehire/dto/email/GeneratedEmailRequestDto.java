package com.samjay.rehire.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratedEmailRequestDto {

    @NotBlank(message = "Email subject is required")
    String subject;

    @NotBlank(message = "Email body is required")
    String body;

}