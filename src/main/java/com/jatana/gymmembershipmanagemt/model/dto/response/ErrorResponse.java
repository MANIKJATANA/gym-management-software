package com.jatana.gymmembershipmanagemt.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for error cases")
public class ErrorResponse {
    @Schema(description = "Timestamp when the error occurred", example = "2024-01-01T10:00:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "HTTP status description", example = "Bad Request")
    private String error;
    
    @Schema(description = "Detailed error message", example = "Invalid member ID provided")
    private String message;
    
    @Schema(description = "API endpoint path where error occurred", example = "/api/member")
    private String path;
}
