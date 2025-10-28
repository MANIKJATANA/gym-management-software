package com.jatana.gymmembershipmanagemt.repo;

import com.jatana.gymmembershipmanagemt.model.Payment;
import com.jatana.gymmembershipmanagemt.model.dto.response.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, String> {

    @Query("""
        select p from Payment p
        where p.membershipId = :membershipId
        
""")
    List<Payment> getPaymentsByMembershipId(@Param("membershipId") String membershipId);
}
