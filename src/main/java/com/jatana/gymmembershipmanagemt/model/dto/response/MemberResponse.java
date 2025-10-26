package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import lombok.Builder;
import lombok.With;

import java.time.LocalDate;
import java.util.List;

@Builder
@With
public record MemberResponse(

        String memberId,

        String firstName,
        String lastName,
        String fullName,

        LocalDate dateOfBirth,
        int age,

        Gender gender,

        String phoneNumber,
        String email,
        String address,

        MemberStatus memberStatus,

        String photoUrl,

        List<MembershipResponse> membershipHistory,
        List<MemberDocumentResponse> documents

) {

}
