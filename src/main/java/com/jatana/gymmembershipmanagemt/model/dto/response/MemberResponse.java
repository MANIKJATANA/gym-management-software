package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.With;

import java.time.LocalDate;
import java.util.List;

@Builder
@With
@Schema(description = "Response object containing detailed member information")
public record MemberResponse(
        @Schema(description = "Unique identifier of the member", example = "MEM123")
        String memberId,

        @Schema(description = "Member's first name", example = "John")
        String firstName,
        
        @Schema(description = "Member's last name", example = "Doe")
        String lastName,
        
        @Schema(description = "Member's full name", example = "John Doe")
        String fullName,

        @Schema(description = "Member's date of birth", example = "1990-01-01")
        LocalDate dateOfBirth,
        
        @Schema(description = "Member's age", example = "33")
        int age,

        @Schema(description = "Member's gender", example = "MALE")
        Gender gender,

        @Schema(description = "Member's phone number", example = "+1234567890")
        String phoneNumber,
        
        @Schema(description = "Member's email address", example = "john.doe@example.com")
        String email,
        
        @Schema(description = "Member's address", example = "123 Main St, City, Country")
        String address,

        @Schema(description = "Current status of the member", example = "ACTIVE")
        MemberStatus memberStatus,

        @Schema(description = "URL of member's photo", example = "https://cloudinary.com/photos/member123.jpg")
        String photoUrl,

        @Schema(description = "List of all memberships associated with the member")
        List<MembershipResponse> membershipHistory,
        
        @Schema(description = "List of all documents uploaded by the member")
        List<MemberDocumentResponse> documents
) {

}
