package com.samjay.rehire.dto.job;

import com.samjay.rehire.constants.CartType;

import java.util.List;
import java.util.UUID;

public record JobApplicationDto(UUID applicationId, String fullName, String email, String phoneNumber,
                                String cvFilePath,
                                String cvContent,
                                List<FormResponseDto> formResponses,
                                CartType cartStatus,
                                boolean isEmailFinalized
) {
}