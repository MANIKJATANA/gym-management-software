package com.jatana.gymmembershipmanagemt.model.dto.response;



import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed response containing membership, payments, and plan information")
public record MembershipDetailResponse(
        @Schema(description = "Basic membership information")
        MembershipResponse membershipResponse,
        
        @Schema(description = "List of all payments made for this membership")
        List<PaymentResponse> paymentResponses,
        
        @Schema(description = "Details of the membership plan")
        PlanResponse planResponse
) {
}
