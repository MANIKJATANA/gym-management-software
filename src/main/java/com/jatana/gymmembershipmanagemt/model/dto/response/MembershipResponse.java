package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.MembershipStatus;

import java.time.LocalDate;

public record MembershipResponse(
        String memberShipId,
        LocalDate startDate,
        LocalDate endDate,
        double pricePaid,
        MembershipStatus membershipStatus

) {
}
