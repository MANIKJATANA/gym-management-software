package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.DocType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Request object for uploading member documents")
public record MemberDocumentUploadRequest(
        @Schema(description = "Document file to upload", type = "string", format = "binary")
        MultipartFile file,
        
        @Schema(description = "Type of document (PHOTO/AADHAR_CARD/IDENTITY_PROOF/ADDRESS_PROOF/OTHER)", example = "PHOTO")
        DocType docType
) {
}
