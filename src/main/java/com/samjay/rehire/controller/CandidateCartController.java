package com.samjay.rehire.controller;

import com.samjay.rehire.constants.CartType;
import com.samjay.rehire.dto.api.ApiResponse;
import com.samjay.rehire.dto.cart.CartRequest;
import com.samjay.rehire.dto.email.GeneratedEmailDto;
import com.samjay.rehire.dto.email.SendBulkEmailRequest;
import com.samjay.rehire.dto.job.JobApplicationDto;
import com.samjay.rehire.service.CandidateCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CandidateCartController {

    private final CandidateCartService candidateCartService;

    @PostMapping(value = "/add-to-cart")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody CartRequest addToCartRequest) {

        candidateCartService.addCandidates(addToCartRequest);

        return ResponseEntity.noContent().build();

    }

    @GetMapping(value = "/fetch-cart-candidates")
    public ResponseEntity<ApiResponse<List<JobApplicationDto>>> fetchCandidatesInACart(@RequestParam UUID jobId,
                                                                                       @RequestParam CartType cartType) {

        return ResponseEntity.ok(new ApiResponse<>(candidateCartService.getCartCandidates(jobId, cartType)));

    }

    @PostMapping(value = "/remove-from-cart")
    public ResponseEntity<Void> removeFromCart(@Valid @RequestBody CartRequest removeFromCartRequest) {

        candidateCartService.removeCandidates(removeFromCartRequest);

        return ResponseEntity.noContent().build();

    }

    @GetMapping(value = "/generate-email")
    public ResponseEntity<ApiResponse<GeneratedEmailDto>> generateDecisionEmail(@RequestParam UUID jobId,
                                                                                @RequestParam CartType cartType) {

        return ResponseEntity.ok(new ApiResponse<>(candidateCartService.generateEmail(jobId, cartType)));

    }

    @PostMapping(value = "/send-bulk-emails")
    public ResponseEntity<ApiResponse<String>> sendBulkDecisionEmail(@Valid @RequestBody SendBulkEmailRequest sendBulkEmailRequest) {

        return ResponseEntity.ok(new ApiResponse<>(candidateCartService.sendBulkEmails(sendBulkEmailRequest)));

    }

    @PostMapping(value = "/move-candidates")
    public ResponseEntity<Void> moveBetweenCarts(@Valid @RequestBody CartRequest moveBetweenCartRequest) {

        candidateCartService.moveCandidatesBetweenCarts(moveBetweenCartRequest);

        return ResponseEntity.noContent().build();

    }
}