package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;

import java.time.LocalDate;

public record PaymentResponse(
        String paymentId,
        double pricePAid,
        LocalDate paymentDateTime,
        PaymentMethod paymentMethod,
        String transactionId,
        String receiptUrl
) {
}
