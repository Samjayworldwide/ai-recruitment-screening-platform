package com.samjay.rehire.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Schema(name = "Error response", description = "Schema to hold error response information")
public class ApiErrorResponse {

    @Schema(description = "Api path invoked by the client")
    private String apiPath;

    @Schema(description = "Error message representing the error that happened")
    private String errorMessage;

    @Schema(description = "Error code representing the error code that happened")
    private HttpStatus errorCode;

    @Schema(description = "Time information representing when the error happened")
    private String errorResponseTime;

}