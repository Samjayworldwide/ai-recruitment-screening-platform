package com.samjay.rehire.dto.cart;

import com.samjay.rehire.constants.CartType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {

    @NotNull(message = "Job ID cannot be null")
    private UUID jobId;

    @NotNull(message = "Cart type cannt be empty")
    private CartType cartType;

    @NotEmpty(message = "At least one candidate must be provided")
    private List<UUID> candidateIds;

}