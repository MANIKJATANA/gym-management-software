package com.jatana.gymmembershipmanagemt.model.dto.request;

import com.jatana.gymmembershipmanagemt.model.enums.DocType;
import org.springframework.web.multipart.MultipartFile;

public record MemberDocumentUploadRequest(
        MultipartFile file,
        DocType docType
) {
}
