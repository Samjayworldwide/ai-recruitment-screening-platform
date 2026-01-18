package com.samjay.rehire.dto.job;

import java.util.UUID;

public record FormResponseDto(UUID fieldId, String fieldLabel, String response) {
}