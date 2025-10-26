package com.jatana.gymmembershipmanagemt.model.dto.response;

import com.jatana.gymmembershipmanagemt.model.enums.DocType;

public record MemberDocumentResponse(
        String documentId,
        DocType docType,
        String docUrl
) {
}
