package com.samjay.rehire.dto.job;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobResponse {

    private String message;

    private String jobUrl;

}