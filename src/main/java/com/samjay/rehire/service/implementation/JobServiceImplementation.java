package com.samjay.rehire.service.implementation;

import com.samjay.rehire.constants.ApplicationType;
import com.samjay.rehire.constants.FieldType;
import com.samjay.rehire.dto.application.FormFieldResponseRequest;
import com.samjay.rehire.dto.application.SubmitApplicationRequest;
import com.samjay.rehire.dto.job.*;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.model.*;
import com.samjay.rehire.repository.*;
import com.samjay.rehire.service.JobService;
import com.samjay.rehire.util.CloudinaryFileUpload;
import com.samjay.rehire.util.DocumentTextExtractor;
import com.samjay.rehire.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImplementation implements JobService {

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final FormFieldRepository formFieldRepository;

    private final OrganizationRepository organizationRepository;

    private final DocumentTextExtractor documentTextExtractor;

    private final CloudinaryFileUpload cloudinaryFileUpload;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public CreateJobResponse createJob(CreateJobRequest createJobRequest) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Organization organization = organizationRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Organization with email does not exist", HttpStatus.BAD_REQUEST));

        UUID jobid = UUID.randomUUID();

        Job job = new Job();

        job.setTitle(createJobRequest.getTitle());

        job.setId(jobid);

        job.setDescription(createJobRequest.getDescription());

        job.setLocation(createJobRequest.getLocation());

        job.setApplicationDeadline(createJobRequest.getApplicationDeadline());

        job.setApplicationType(createJobRequest.getApplicationType());

        job.setWorkMode(createJobRequest.getWorkMode());

        job.setOrganization(organization);

        job.setApplicationUrl(generateApplicationUrl(createJobRequest.getTitle(), jobid));

        final Job savedJob = jobRepository.save(job);

        savedJob.addFormField(createMandatoryFormField("Full Name", FieldType.TEXT, 1));

        savedJob.addFormField(createMandatoryFormField("Email Address", FieldType.TEXT, 2));

        savedJob.addFormField(createMandatoryFormField("Phone Number", FieldType.TEXT, 3));


        if (savedJob.getApplicationType() == ApplicationType.CV_UPLOAD) {

            savedJob.addFormField(createMandatoryFormField("CV Upload", FieldType.FILE, 4));

        }

        if (savedJob.getApplicationType() == ApplicationType.APPLICATION_FORM) {

            if (createJobRequest.getFormFields() == null || createJobRequest.getFormFields().isEmpty()) {

                throw new ApplicationException("Custom form fields are required for job with APPLICATION FORM type", HttpStatus.BAD_REQUEST);

            }

            for (FormFieldRequest fieldRequest : createJobRequest.getFormFields()) {

                FormField customField = new FormField();

                customField.setLabel(fieldRequest.getLabel());

                customField.setType(fieldRequest.getType());

                customField.setOptions(fieldRequest.getOptions());

                customField.setRequired(fieldRequest.getRequired());

                customField.setDisplayOrder(fieldRequest.getDisplayOrder() != null ? fieldRequest.getDisplayOrder() + 3 : 3);

                savedJob.addFormField(customField);

            }

        } else if (createJobRequest.getFormFields() != null && !createJobRequest.getFormFields().isEmpty()) {

            throw new ApplicationException("Custom form fields are not allowed for job with CV UPLOAD type", HttpStatus.BAD_REQUEST);

        }

        formFieldRepository.saveAll(savedJob.getFormFields());

        organization.addJob(savedJob);

        organizationRepository.save(organization);

        return CreateJobResponse
                .builder()
                .message("Job created successfully")
                .jobUrl(job.getApplicationUrl())
                .build();
    }

    @Transactional
    @Override
    public String submitApplication(UUID id, SubmitApplicationRequest request) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Job not found", HttpStatus.BAD_REQUEST));

        if (job.getApplicationDeadline().isBefore(LocalDateTime.now())) {

            throw new ApplicationException("Job application deadline has passed", HttpStatus.BAD_REQUEST);

        }

        List<FormField> formFields = formFieldRepository.findByJobOrderByDisplayOrder(job);

        Map<UUID, FormField> formFieldMap = formFields
                .stream()
                .collect(Collectors.toMap(FormField::getId, field -> field));

        String cvContent = null;
        String cvFilePath = null;

        if (job.getApplicationType() == ApplicationType.CV_UPLOAD) {

            if (request.getCv() == null || request.getCv().isEmpty()) {

                throw new ApplicationException("CV is required for this job application", HttpStatus.BAD_REQUEST);

            }

            try {

                cvContent = documentTextExtractor.extractTextFromDocument(request.getCv());

                cvFilePath = cloudinaryFileUpload.saveFile(request.getCv());

            } catch (Exception e) {

                throw new ApplicationException("Failed to process CV", HttpStatus.BAD_REQUEST);

            }
        } else if (job.getApplicationType() == ApplicationType.APPLICATION_FORM) {

            if (request.getFormResponses() == null || request.getFormResponses().isEmpty()) {

                throw new ApplicationException("Form responses are required for this job application", HttpStatus.BAD_REQUEST);

            }

            formFields.stream()
                    .filter(FormField::isRequired)
                    .filter(field -> !field.getLabel().equalsIgnoreCase("Full Name")
                            && !field.getLabel().equalsIgnoreCase("Email Address")
                            && !field.getLabel().equalsIgnoreCase("Phone Number"))
                    .forEach(field -> {

                        boolean hasResponse = request
                                .getFormResponses()
                                .stream()
                                .anyMatch(response -> response.getFormFieldId().equals(field.getId()));

                        if (!hasResponse) {

                            throw new ApplicationException("Missing response for required field: " + field.getLabel(), HttpStatus.BAD_REQUEST);

                        }
                    });

            request.getFormResponses()
                    .forEach(response -> {

                        if (!formFieldMap.containsKey(response.getFormFieldId())) {

                            throw new ApplicationException("Invalid form field ID: " + response.getFormFieldId(), HttpStatus.BAD_REQUEST);

                        }
                    });
        }

        JobApplication application = JobApplication
                .builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .cvContent(cvContent)
                .cvFilePath(cvFilePath)
                .build();

        job.addJobApplication(application);

        if (job.getApplicationType() == ApplicationType.APPLICATION_FORM) {

            for (FormFieldResponseRequest fieldResponseRequest : request.getFormResponses()) {

                FormField field = formFieldMap.get(fieldResponseRequest.getFormFieldId());

                FormResponse formResponse = new FormResponse();

                formResponse.setResponse(fieldResponseRequest.getResponse());

                application.addResponse(formResponse);

                field.addResponse(formResponse);
            }
        }

        jobApplicationRepository.save(application);

        return "Job application submitted successfully";

    }

    @Override
    public List<OrganizationJobResponse> getOrganizationJobs() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Organization organization = organizationRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Organization with email does not exist", HttpStatus.BAD_REQUEST));

        return jobRepository.findByOrganization(organization)
                .stream()
                .map(this::mapToOrganizationJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse fetchApplicationForm(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Job not found", HttpStatus.BAD_REQUEST));

        if (job.getApplicationDeadline().isBefore(LocalDateTime.now())) {

            throw new ApplicationException("Job application deadline has passed", HttpStatus.BAD_REQUEST);

        }

        return mapToJobResponse(job, formFieldRepository.findByJobOrderByDisplayOrder(job));

    }

    @Override
    public JobApplicantsDto getJobApplicants(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Job not found", HttpStatus.BAD_REQUEST));

        List<JobApplication> applications = jobApplicationRepository
                .findByJob(job)
                .stream()
                .filter(app -> !app.isEmailFinalized())
                .toList();

        List<JobApplicationDto> applicationDtos = applications
                .stream()
                .map(modelMapper::toJobApplicationDto)
                .toList();

        return new JobApplicantsDto(job.getId(), job.getTitle(), applicationDtos);

    }

    private FormField createMandatoryFormField(String label, FieldType fieldType, int displayOrder) {

        FormField field = new FormField();

        field.setLabel(label);

        field.setType(fieldType);

        field.setRequired(true);

        field.setDisplayOrder(displayOrder);

        return field;

    }

    private String generateApplicationUrl(String title, UUID jobId) {

        String baseUrl = "https://rehire.com";

        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        return String.format("%s/jobs/%s/%s", baseUrl, slug, jobId.toString());

    }

    private OrganizationJobResponse mapToOrganizationJobResponse(Job job) {

        return OrganizationJobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .workMode(job.getWorkMode())
                .jobStatus(job.getJobStatus())
                .applicationDeadline(job.getApplicationDeadline())
                .applicationType(job.getApplicationType())
                .applicationUrl(job.getApplicationUrl())
                .organizationName(job.getOrganization().getName())
                .createdAt(job.getCreatedAt())
                .build();
    }

    private JobResponse mapToJobResponse(Job job, List<FormField> formFields) {

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .workMode(job.getWorkMode())
                .jobStatus(job.getJobStatus())
                .applicationDeadline(job.getApplicationDeadline())
                .applicationType(job.getApplicationType())
                .applicationUrl(job.getApplicationUrl())
                .organizationName(job.getOrganization().getName())
                .createdAt(job.getCreatedAt())
                .formFields(formFields.stream()
                        .map(field -> FormFieldResponse.builder()
                                .id(field.getId())
                                .label(field.getLabel())
                                .type(field.getType())
                                .options(field.getOptions())
                                .required(field.isRequired())
                                .displayOrder(field.getDisplayOrder())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}