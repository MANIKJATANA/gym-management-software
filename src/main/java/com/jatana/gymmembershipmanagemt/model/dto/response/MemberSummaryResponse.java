package com.jatana.gymmembershipmanagemt.model.dto.response;


import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;

import java.time.LocalDate;

public record MemberSummaryResponse(
        String memberId,
        String fullName,
        int age,
        Gender gender,
        String phoneNumber,
        String email,
        MemberStatus memberStatus,
        String photoUrl,
        LocalDate membershipEndDate


) {
}

