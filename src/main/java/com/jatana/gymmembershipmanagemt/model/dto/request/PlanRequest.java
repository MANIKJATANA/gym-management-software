package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating or updating a membership plan")
public record PlanRequest(
        @Schema(description = "Name of the plan", example = "Gold Plan")
        String planName,

        @Schema(description = "Duration of the plan in months", example = "12")
        int durationMonths,

        @Schema(description = "Price of the plan", example = "99.99")
        double price,

        @Schema(description = "Detailed description of the plan", example = "Premium membership plan with access to all facilities")
        String description
) {
} 
