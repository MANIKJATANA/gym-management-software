package com.jatana.gymmembershipmanagemt.model.dto.response;

public record PlanResponse (
        String planId,
        String planName,
        int duration_months,
        double price,
        String description
){

}
