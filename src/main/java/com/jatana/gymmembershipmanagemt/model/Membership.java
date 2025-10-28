package com.jatana.gymmembershipmanagemt.model;

import com.jatana.gymmembershipmanagemt.model.enums.MembershipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import com.jatana.gymmembershipmanagemt.util.UuidGenerator;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Membership {
    @Id
    private String membershipId;
    private String memberId;

    private String planId;

    private LocalDate startDate;
    private LocalDate endDate;

    private double pricePaid;

    private MembershipStatus membershipStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureId() {
        if (membershipId == null || membershipId.isEmpty()) {
            membershipId = UuidGenerator.generateId();
        }
    }

}
