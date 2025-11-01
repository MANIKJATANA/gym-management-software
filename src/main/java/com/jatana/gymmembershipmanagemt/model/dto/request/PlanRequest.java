package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlanRequest(
        String planName,

        int durationMonths,

        double price,

        String description

) {
} 
