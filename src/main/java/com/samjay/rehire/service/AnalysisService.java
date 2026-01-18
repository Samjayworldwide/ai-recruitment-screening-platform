package com.samjay.rehire.service;

import com.samjay.rehire.dto.analysis.CandidateAnalysisResponse;

import java.util.UUID;

public interface AnalysisService {

    CandidateAnalysisResponse analyzeCandidates(UUID id, String prompt);

}