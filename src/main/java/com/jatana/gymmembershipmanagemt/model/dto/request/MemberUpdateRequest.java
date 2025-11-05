package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request object for updating an existing member")
public record MemberUpdateRequest(
        @Schema(description = "Updated first name", example = "John")
        String firstName,
        
        @Schema(description = "Updated last name", example = "Doe")
        String lastName,

        @Schema(description = "Updated date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,

        @Schema(description = "Updated gender", example = "MALE")
        Gender gender,

        @Schema(description = "Updated phone number", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "Updated email address", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "Updated address", example = "123 Main St, City, Country")
        String address
) {
}
