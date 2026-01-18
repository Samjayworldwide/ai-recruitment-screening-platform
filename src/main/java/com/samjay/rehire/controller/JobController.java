package com.samjay.rehire.controller;

import com.samjay.rehire.dto.api.ApiResponse;
import com.samjay.rehire.dto.application.SubmitApplicationRequest;
import com.samjay.rehire.dto.job.*;
import com.samjay.rehire.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class JobController {

    private final JobService jobService;

    @PostMapping(value = "/create-job")
    public ResponseEntity<ApiResponse<CreateJobResponse>> createJob(@Valid @RequestBody CreateJobRequest createJobRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(jobService.createJob(createJobRequest)));

    }

    @PostMapping(value = "/submit-application")
    public ResponseEntity<ApiResponse<String>> submitApplication(@RequestParam UUID id,
                                                                 @Valid SubmitApplicationRequest submitApplicationRequest) {

        return ResponseEntity.ok(new ApiResponse<>(jobService.submitApplication(id, submitApplicationRequest)));

    }

    @GetMapping(value = "/fetch-organization-jobs")
    public ResponseEntity<ApiResponse<List<OrganizationJobResponse>>> fetchOrganizationJobs() {

        return ResponseEntity.ok(new ApiResponse<>(jobService.getOrganizationJobs()));

    }

    @GetMapping(value = "/fetch-application-form")
    public ResponseEntity<ApiResponse<JobResponse>> getApplicationForm(@RequestParam UUID id) {

        return ResponseEntity.ok(new ApiResponse<>(jobService.fetchApplicationForm(id)));

    }

    @GetMapping(value = "/fetch-job-applicants")
    public ResponseEntity<ApiResponse<JobApplicantsDto>> getJobApplicants(@RequestParam UUID id) {

        return ResponseEntity.ok(new ApiResponse<>(jobService.getJobApplicants(id)));

    }
}