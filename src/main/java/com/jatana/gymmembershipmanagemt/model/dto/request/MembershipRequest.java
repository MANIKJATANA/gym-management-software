package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MembershipRequest(
        String planId,
        LocalDate startDate,
        LocalDate endDate,

        double pricePaid,
        LocalDateTime paymentDateTime,
        PaymentMethod paymentMethod,
        String transactionId

) {
}
