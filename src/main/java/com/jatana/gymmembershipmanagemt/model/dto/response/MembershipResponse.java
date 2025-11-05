package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.MembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response object containing membership information")
public record MembershipResponse(
        @Schema(description = "Unique identifier of the membership", example = "MEM123-2024")
        String membershipId,
        
        @Schema(description = "Start date of the membership", example = "2024-01-01")
        LocalDate startDate,
        
        @Schema(description = "End date of the membership", example = "2024-12-31")
        LocalDate endDate,
        
        @Schema(description = "Amount paid for the membership", example = "999.99")
        double pricePaid,
        
        @Schema(description = "Current status of the membership", example = "ACTIVE")
        MembershipStatus membershipStatus
) {
}
