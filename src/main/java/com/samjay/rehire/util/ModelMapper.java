package com.samjay.rehire.util;

import com.samjay.rehire.constants.CartType;
import com.samjay.rehire.dto.job.FormResponseDto;
import com.samjay.rehire.dto.job.JobApplicationDto;
import com.samjay.rehire.model.JobApplication;
import com.samjay.rehire.repository.FormResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModelMapper {

    private final FormResponseRepository formResponseRepository;

    public JobApplicationDto toJobApplicationDto(JobApplication application) {

        List<FormResponseDto> responseDtos = formResponseRepository.findByJobApplication(application)
                .stream()
                .map(response -> new FormResponseDto(
                        response.getFormField().getId(),
                        response.getFormField().getLabel(),
                        response.getResponse()
                ))
                .toList();

        CartType cartStatus = application.getCart() != null ? application.getCart().getCartType() : null;

        return new JobApplicationDto(application.getId(), application.getFullName(), application.getEmail(),
                application.getPhoneNumber(),
                application.getCvFilePath(),
                application.getCvContent(),
                responseDtos,
                cartStatus,
                application.isEmailFinalized()
        );
    }
}