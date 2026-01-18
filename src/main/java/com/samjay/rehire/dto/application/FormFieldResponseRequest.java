package com.samjay.rehire.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldResponseRequest {

    @NotNull(message = "Form field ID is required")
    private UUID formFieldId;

    @NotBlank(message = "Response is required")
    private String response;

}