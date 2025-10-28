package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        double pricePaid,
        LocalDateTime paymentDateTime,
        PaymentMethod paymentMethod,
        String transactionId,
        String receiptUrl
) {
}
