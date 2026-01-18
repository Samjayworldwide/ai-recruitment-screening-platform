package com.samjay.rehire.dto.analysis;

import com.samjay.rehire.dto.job.JobApplicationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnalysisResponse {

    private String aiMessage;

    private List<JobApplicationDto> matchingCandidates;

}