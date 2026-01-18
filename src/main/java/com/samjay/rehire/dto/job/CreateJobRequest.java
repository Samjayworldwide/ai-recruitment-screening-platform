package com.samjay.rehire.dto.job;

import com.samjay.rehire.constants.ApplicationType;
import com.samjay.rehire.constants.WorkMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotNull(message = "Application deadline is required")
    @Future(message = "Application deadline must be in the future")
    private LocalDateTime applicationDeadline;

    @NotNull(message = "Application type is required")
    private ApplicationType applicationType;

    @NotBlank(message = "Job location is required")
    private String location;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    private List<FormFieldRequest> formFields;

}