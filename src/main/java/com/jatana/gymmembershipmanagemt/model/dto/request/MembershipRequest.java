package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Request object for creating a new membership")
public record MembershipRequest(
        @Schema(description = "ID of the selected membership plan", example = "GOLD-PLAN")
        String planId,
        
        @Schema(description = "Start date of the membership", example = "2024-01-01")
        LocalDate startDate,
        
        @Schema(description = "End date of the membership", example = "2024-12-31")
        LocalDate endDate,

        @Schema(description = "Amount paid for the membership", example = "999.99")
        double pricePaid,
        
        @Schema(description = "Date and time of the payment", example = "2024-01-01T10:00:00")
        LocalDateTime paymentDateTime,
        
        @Schema(description = "Method of payment (CASH/CARD)", example = "CARD")
        PaymentMethod paymentMethod,
        
        @Schema(description = "Transaction ID for the payment", example = "TXN123456789")
        String transactionId
) {
}
