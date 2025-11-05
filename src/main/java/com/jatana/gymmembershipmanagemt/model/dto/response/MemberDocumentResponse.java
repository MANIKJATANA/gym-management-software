package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.DocType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing member document information")
public record MemberDocumentResponse(
        @Schema(description = "Unique identifier of the document", example = "DOC123")
        String documentId,
        
        @Schema(description = "Type of document (PHOTO/AADHAR_CARD/IDENTITY_PROOF/ADDRESS_PROOF/OTHER)", example = "PHOTO")
        DocType docType,
        
        @Schema(description = "URL to access the document", example = "https://cloudinary.com/documents/member123/photo.jpg")
        String docUrl
) {
}
