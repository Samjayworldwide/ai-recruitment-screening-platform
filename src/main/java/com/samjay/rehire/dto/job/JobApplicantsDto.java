package com.samjay.rehire.dto.job;


import java.util.List;
import java.util.UUID;

public record JobApplicantsDto(UUID jobId, String title, List<JobApplicationDto> applications) {
}