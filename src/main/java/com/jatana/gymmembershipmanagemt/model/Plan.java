package com.jatana.gymmembershipmanagemt.model;

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
public class Plan {
    @Id
    private String planId;

    private String planName;

    private int duration_months;

    private double price;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureId() {
        if (planId == null || planId.isEmpty()) {
            planId = com.jatana.gymmembershipmanagemt.util.UuidGenerator.generateId();
        }
    }
}
