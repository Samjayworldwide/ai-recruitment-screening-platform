package com.samjay.rehire.dto.job;

import com.samjay.rehire.constants.ApplicationType;
import com.samjay.rehire.constants.JobStatus;
import com.samjay.rehire.constants.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private UUID id;

    private String title;

    private String description;

    private String location;

    private WorkMode workMode;

    private JobStatus jobStatus;

    private LocalDateTime applicationDeadline;

    private ApplicationType applicationType;

    private String applicationUrl;

    private String organizationName;

    private LocalDateTime createdAt;

    private List<FormFieldResponse> formFields;

}