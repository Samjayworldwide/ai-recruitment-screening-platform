package com.samjay.rehire.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samjay.rehire.dto.analysis.CandidateAnalysisResponse;
import com.samjay.rehire.dto.job.JobApplicationDto;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.langchain4j.LangChain4jAssistant;
import com.samjay.rehire.model.*;
import com.samjay.rehire.repository.*;
import com.samjay.rehire.service.AnalysisService;
import com.samjay.rehire.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImplementation implements AnalysisService {

    private final LangChain4jAssistant langChain4jAssistant;

    private final JobApplicationRepository jobApplicationRepository;

    private final JobRepository jobRepository;

    private final FormResponseRepository formResponseRepository;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImplementation.class);

    @Override
    public CandidateAnalysisResponse analyzeCandidates(UUID id, String prompt) {

        Job job = jobRepository
                .findById(id)
                .orElseThrow(() -> new ApplicationException("Job not found", HttpStatus.BAD_REQUEST));

        List<JobApplication> allApplications = jobApplicationRepository.findByJob(job);

        List<JobApplication> nonFinalizedApplications = allApplications
                .stream()
                .filter(app -> !app.isEmailFinalized())
                .collect(Collectors.toList());

        Map<UUID, List<FormResponse>> formResponsesByApplication = nonFinalizedApplications
                .stream()
                .flatMap(app -> formResponseRepository
                        .findByJobApplication(app)
                        .stream())
                .collect(Collectors.groupingBy(response -> response
                        .getJobApplication()
                        .getId()));

        String candidatesInfo = nonFinalizedApplications
                .stream()
                .map(app -> {

                    List<FormResponse> responses = formResponsesByApplication.getOrDefault(app.getId(), List.of());

                    String formResponsesText = responses.stream()
                            .map(r -> r.getFormField().getLabel() + ": " + r.getResponse())
                            .collect(Collectors.joining("\n"));

                    return String.format("""
                                    Candidate ID: %s
                                    Full Name: %s
                                    CV: %s
                                    Form Responses:
                                    %s
                                    ---
                                    """,
                            app.getId(),
                            app.getFullName(),
                            app.getCvContent() != null ? app.getCvContent() : "No CV provided",
                            formResponsesText
                    );

                })
                .collect(Collectors.joining("\n"));

        logger.info("CANDIDATES INFO {} ", candidatesInfo);

        String aiResponse = langChain4jAssistant.filterCandidates(job.getId().toString(), candidatesInfo, prompt);

        logger.info("RESPONSE FROM OLLAMA AI {} ", aiResponse);

        return parseAiResponse(aiResponse, nonFinalizedApplications);

    }

    private CandidateAnalysisResponse parseAiResponse(String aiResponse, List<JobApplication> applications) {

        try {

            if (aiResponse == null || aiResponse.isBlank()) {

                return new CandidateAnalysisResponse("Could not generate any response", List.of());

            }

            String cleanedResponse = cleanAiResponse(aiResponse);

            logger.info("This is the cleaned response {}", cleanedResponse);

            String jsonPart = extractValidJson(cleanedResponse);

            if (jsonPart == null) {

                return new CandidateAnalysisResponse("No valid JSON found in AI response.", List.of());

            }

            JsonNode matchesNode = parseMatchesNode(jsonPart);

            if (matchesNode == null || !matchesNode.isArray() || matchesNode.isEmpty()) {

                return new CandidateAnalysisResponse("No matches found.", List.of());

            }

            return buildCandidateAnalysis(matchesNode, applications);

        } catch (Exception e) {

            return new CandidateAnalysisResponse("An unexpected error ocurred trying to filter candidates based on prompt", List.of());

        }
    }

    private String cleanAiResponse(String aiResponse) {

        return aiResponse.replaceAll("<\\|.*?\\|>", "")
                .replaceAll("(?m)^\\s*assistant\\s*$", "")
                .replaceAll("(?m)^[ \t]*\r?\n", "")
                .trim();
    }

    private String extractValidJson(String cleanedResponse) {

        Pattern jsonPattern = Pattern.compile("\\{\\s*\"matches\"\\s*:\\s*\\[.*?]\\s*}", Pattern.DOTALL);

        Matcher matcher = jsonPattern.matcher(cleanedResponse);

        if (matcher.find()) {

            String candidate = matcher.group();

            if (isValidJson(candidate)) {

                return candidate;

            }
        }

        return null;

    }

    private boolean isValidJson(String json) {

        try {

            new ObjectMapper().readTree(json);

            return true;

        } catch (Exception e) {

            return false;

        }
    }

    private JsonNode parseMatchesNode(String jsonPart) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(jsonPart);

            return root.get("matches");

        } catch (Exception ex) {

            return null;

        }
    }

    private CandidateAnalysisResponse buildCandidateAnalysis(JsonNode matchesNode, List<JobApplication> applications) {

        StringBuilder aiMessageBuilder = new StringBuilder();

        aiMessageBuilder.append(matchesNode.size()).append(" candidates matched your criteria:\n");

        List<UUID> matchingIds = new ArrayList<>();

        for (JsonNode match : matchesNode) {

            String candidateIdStr = match.path("candidateId").asText(null);

            String name = match.path("fullName").asText("Unknown");

            String reason = match.path("reason").asText("No reason provided");

            if (candidateIdStr != null) {

                if (tryAddUuid(candidateIdStr, matchingIds)) {

                    aiMessageBuilder.append("- ").append(name).append(": ").append(reason).append("\n");

                } else {

                    aiMessageBuilder.append("- ").append(name).append(" (Skipped: invalid candidateId format)\n");

                }
            } else {

                aiMessageBuilder.append("- ").append(name).append(" (Skipped: missing candidateId)\n");

            }
        }

        List<JobApplicationDto> matchingCandidates = applications
                .stream()
                .filter(app -> matchingIds.contains(app.getId()))
                .map(modelMapper::toJobApplicationDto)
                .collect(Collectors.toList());

        return new CandidateAnalysisResponse(aiMessageBuilder.toString().trim(), matchingCandidates);

    }

    private boolean tryAddUuid(String candidateIdStr, List<UUID> matchingIds) {

        try {

            UUID id = UUID.fromString(candidateIdStr);

            matchingIds.add(id);

            return true;

        } catch (IllegalArgumentException ex) {

            return false;

        }
    }

}