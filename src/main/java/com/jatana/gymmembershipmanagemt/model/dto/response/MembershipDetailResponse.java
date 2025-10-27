package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.Plan;

import java.util.List;

public record MembershipDetailResponse(
        MembershipResponse membershipResponse,
        List<PaymentResponse>  paymentResponses,
        PlanResponse planResponse
) {
}
