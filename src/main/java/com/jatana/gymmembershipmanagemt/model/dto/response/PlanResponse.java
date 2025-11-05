package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing membership plan information")
public record PlanResponse (
        @Schema(description = "Unique identifier of the plan", example = "GOLD-PLAN")
        String planId,
        
        @Schema(description = "Name of the plan", example = "Gold Plan")
        String planName,
        
        @Schema(description = "Duration of the plan in months", example = "12")
        int durationMonths,
        
        @Schema(description = "Price of the plan", example = "999.99")
        double price,
        
        @Schema(description = "Detailed description of the plan", example = "Premium membership with access to all facilities")
        String description
){

}
