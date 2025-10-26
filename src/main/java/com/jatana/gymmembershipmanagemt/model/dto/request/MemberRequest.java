package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.Gender;

import java.time.LocalDate;

public record MemberRequest(
        String memberId,
        String firstName,
        String lastName,

        LocalDate dateOfBirth,

        Gender gender,

        String phoneNumber,
        String email,

        String address



) {
}
