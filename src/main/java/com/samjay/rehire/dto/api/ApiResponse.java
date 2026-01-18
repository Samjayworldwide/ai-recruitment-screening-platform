package com.samjay.rehire.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Schema(name = "API Response", description = "Schema to hold successful response information")
@RequiredArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "Status Message in the Response")
    private String responseMessage;

    @Schema(description = "Response body in the Response")
    private T responseBody;

    @Schema(description = "Response time in the Response")
    private String responseTime;

    public ApiResponse(String responseMessage) {

        this.responseMessage = responseMessage;

        this.responseTime = LocalDateTime.now().toString();

    }

    public ApiResponse(String responseMessage, T responseBody) {

        this.responseMessage = responseMessage;

        this.responseBody = responseBody;

        this.responseTime = LocalDateTime.now().toString();

    }

    public ApiResponse(T responseBody) {

        this.responseBody = responseBody;

        this.responseTime = LocalDateTime.now().toString();

    }
}