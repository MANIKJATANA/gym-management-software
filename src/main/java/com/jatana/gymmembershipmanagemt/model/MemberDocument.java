package com.jatana.gymmembershipmanagemt.model;

import com.jatana.gymmembershipmanagemt.model.enums.DocType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MemberDocument {
    @Id
    private String documentId;

    private String memberId;

    private DocType documentType;

    private String url;

    private LocalDateTime uploadedAt;

    @PrePersist
    public void ensureId() {
        if (documentId == null || documentId.isEmpty()) {
            documentId = com.jatana.gymmembershipmanagemt.util.UuidGenerator.generateId();
        }
    }
}
