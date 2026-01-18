package com.samjay.rehire.langchain4j;

import com.samjay.rehire.dto.email.EmailBody;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface LangChain4jAssistant {

    @SystemMessage("""
                You are an expert HR recruiter analyzing job applications.
            
                # RESPONSE FORMAT RULES
                - You must output ONLY a valid JSON object.
                - DO NOT include markdown, code fences, explanations, or any text outside the JSON.
                - Your output MUST start with "{" and end with "}".
                - If no matches are found, return: {"matches": []}
            
                # TASK
                You will be given:
                - candidatesInfo: {{candidatesInfo}}
                - userPrompt: {{userPrompt}}
            
                ONLY use the provided candidatesInfo to analyze and filter candidates.
                DO NOT invent candidates that are not in candidatesInfo.
            
                Output format:
                {
                    "matches": [
                        {
                            "candidateId": "<UUID from candidatesInfo>",
                            "fullName": "<Full name from candidatesInfo>",
                            "reason": "<Reason this candidate matched>"
                        }
                    ]
                }
                - You can output more than one candidate if they match the userPrompt: {{userPrompt}}
            """)
    String filterCandidates(@MemoryId String jobId, @V("candidatesInfo") String candidatesInfo, @UserMessage String userPrompt);

    @UserMessage("Write only the email body. Do not include subject, explanations, or JSON. Just return the plain text body.")
    EmailBody generateEmailBody(String prompt);
}