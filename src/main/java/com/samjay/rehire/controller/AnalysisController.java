package com.samjay.rehire.controller;

import com.samjay.rehire.dto.analysis.CandidateAnalysisResponse;
import com.samjay.rehire.dto.api.ApiResponse;
import com.samjay.rehire.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping(value = "/filter")
    public ResponseEntity<ApiResponse<CandidateAnalysisResponse>> filterCandidatesBasedOnPrompt(@RequestParam UUID id,
                                                                                                @RequestParam String prompt) {

        return ResponseEntity.ok(new ApiResponse<>(analysisService.analyzeCandidates(id, prompt)));

    }
}