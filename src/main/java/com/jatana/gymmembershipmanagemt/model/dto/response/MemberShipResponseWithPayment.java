package com.jatana.gymmembershipmanagemt.model.dto.response;

import java.util.List;

public record MemberShipResponseWithPayment(
        MembershipResponse membershipResponse,
        List<PaymentResponse>  paymentResponses
) {
}
