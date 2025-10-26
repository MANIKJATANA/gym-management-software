package com.jatana.gymmembershipmanagemt.model;

import com.jatana.gymmembershipmanagemt.model.enums.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {
    @Id
    private String paymentId;
    private String memberShipId;

    private double pricePaid;
    private LocalDateTime paymentDateTime;
    private PaymentMethod paymentMethod;

    private String transactionId;
    private String receiptUrl;

    private LocalDateTime createdAt;

    @PrePersist
    public void ensureId() {
        if (paymentId == null || paymentId.isEmpty()) {
            paymentId = com.jatana.gymmembershipmanagemt.util.UuidGenerator.generateId();
        }
    }
}
