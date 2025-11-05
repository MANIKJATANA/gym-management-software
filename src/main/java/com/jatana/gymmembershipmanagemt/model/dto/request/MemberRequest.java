package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request object for creating a new member")
public record MemberRequest(
        @Schema(description = "Unique identifier for the member", example = "MEM123")
        String memberId,
        
        @Schema(description = "Member's first name", example = "John")
        String firstName,
        
        @Schema(description = "Member's last name", example = "Doe")
        String lastName,

        @Schema(description = "Member's date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,

        @Schema(description = "Member's gender", example = "MALE")
        Gender gender,

        @Schema(description = "Member's phone number", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "Member's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "Member's complete address", example = "123 Main St, City, Country")
        String address
) {
}
