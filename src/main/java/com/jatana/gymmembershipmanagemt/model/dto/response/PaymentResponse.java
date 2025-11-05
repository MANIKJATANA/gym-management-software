package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response object containing payment information")
public record PaymentResponse(
        @Schema(description = "Unique identifier of the payment", example = "PAY123")
        String paymentId,
        
        @Schema(description = "Amount paid", example = "999.99")
        double pricePaid,
        
        @Schema(description = "Date and time of payment", example = "2024-01-01T10:00:00")
        LocalDateTime paymentDateTime,
        
        @Schema(description = "Method of payment (CASH/CARD)", example = "CARD")
        PaymentMethod paymentMethod,
        
        @Schema(description = "Transaction ID from payment gateway", example = "TXN123456789")
        String transactionId,
        
        @Schema(description = "URL to download payment receipt", example = "https://cloudinary.com/receipts/pay123.pdf")
        String receiptUrl
) {
}
