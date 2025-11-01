package com.jatana.gymmembershipmanagemt.model;

import com.jatana.gymmembershipmanagemt.model.enums.MembershipStatus;
import jakarta.persistence.*;
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

@Table(indexes = {
        @Index(name = "idx_membership_member_end_date", columnList = "member_id, end_date"),
        @Index(name = "idx_membership_id", columnList = "membership_id")
})
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
