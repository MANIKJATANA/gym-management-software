package com.jatana.gymmembershipmanagemt.model.dto.response;

import java.util.List;

public record MembershipResponseWithPayment(
        MembershipResponse membershipResponse,
        List<PaymentResponse>  paymentResponses
) {
}
