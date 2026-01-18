package com.samjay.rehire.service;

import com.samjay.rehire.dto.application.SubmitApplicationRequest;
import com.samjay.rehire.dto.job.*;

import java.util.List;
import java.util.UUID;

public interface JobService {

    CreateJobResponse createJob(CreateJobRequest createJobRequest);

    String submitApplication(UUID id, SubmitApplicationRequest request);

    List<OrganizationJobResponse> getOrganizationJobs();

    JobResponse fetchApplicationForm(UUID id);

    JobApplicantsDto getJobApplicants(UUID id);

}