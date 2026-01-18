package com.samjay.rehire.dto.email;

import com.samjay.rehire.constants.CartType;
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
public class SendBulkEmailRequest {

    @NotNull(message = "Job ID cannot be empty")
    private UUID jobId;

    @NotNull(message = "Cart type cannot be null")
    private CartType cartType;

    @NotNull(message = "Email subject and body cannot be null")
    private GeneratedEmailRequestDto generatedEmailRequestDto;

}