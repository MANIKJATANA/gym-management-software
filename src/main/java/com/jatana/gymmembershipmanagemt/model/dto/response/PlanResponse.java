package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlanResponse (
        String planId,
        String planName,
        int durationMonths,
        double price,
        String description
){

}
