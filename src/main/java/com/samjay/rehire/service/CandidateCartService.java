package com.samjay.rehire.service;

import com.samjay.rehire.constants.CartType;
import com.samjay.rehire.dto.cart.CartRequest;
import com.samjay.rehire.dto.email.GeneratedEmailDto;
import com.samjay.rehire.dto.email.SendBulkEmailRequest;
import com.samjay.rehire.dto.job.JobApplicationDto;

import java.util.List;
import java.util.UUID;

public interface CandidateCartService {

    void addCandidates(CartRequest addToCartRequest);

    List<JobApplicationDto> getCartCandidates(UUID jobId, CartType cartType);

    void removeCandidates(CartRequest removeFromCartRequest);

    GeneratedEmailDto generateEmail(UUID jobId, CartType cartType);

    String sendBulkEmails(SendBulkEmailRequest sendBulkEmailRequest);

    void moveCandidatesBetweenCarts(CartRequest moveBetweenCartRequest);

}