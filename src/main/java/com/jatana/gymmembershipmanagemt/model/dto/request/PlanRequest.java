package com.jatana.gymmembershipmanagemt.model.dto.request;

public record PlanRequest(
        String planName,

        int duration_months,

        double price,

        String description

) {
}
