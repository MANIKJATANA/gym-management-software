package com.jatana.gymmembershipmanagemt.model.dto.response;


import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response object containing summarized member information")
public record MemberSummaryResponse(
        @Schema(description = "Unique identifier of the member", example = "MEM123")
        String memberId,
        
        @Schema(description = "Full name of the member", example = "John Doe")
        String fullName,
        
        @Schema(description = "Age of the member", example = "33")
        int age,
        
        @Schema(description = "Gender of the member", example = "MALE")
        Gender gender,
        
        @Schema(description = "Phone number of the member", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "Email address of the member", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "Current status of the member", example = "ACTIVE")
        MemberStatus memberStatus,
        
        @Schema(description = "URL of member's photo", example = "https://cloudinary.com/photos/member123.jpg")
        String photoUrl,
        
        @Schema(description = "End date of the current/latest membership", example = "2024-12-31")
        LocalDate membershipEndDate
) {
}

