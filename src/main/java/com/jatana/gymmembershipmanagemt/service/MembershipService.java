package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.Membership;
import com.jatana.gymmembershipmanagemt.model.Payment;
import com.jatana.gymmembershipmanagemt.model.Plan;
import com.jatana.gymmembershipmanagemt.model.dto.request.MembershipRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.*;
import com.jatana.gymmembershipmanagemt.model.enums.MembershipStatus;
import com.jatana.gymmembershipmanagemt.repo.MembershipRepo;
import com.jatana.gymmembershipmanagemt.repo.PaymentRepo;
import com.jatana.gymmembershipmanagemt.repo.PlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipDetailResponse;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepo membershipRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PlanRepo planRepo;

    public MembershipResponse addMembership(String memberId, MembershipRequest membershipRequest) {



        Membership membership = new Membership();
        membership.setMemberId(memberId);
        membership.setPlanId(membershipRequest.planId());
        membership.setStartDate(membershipRequest.startDate());
        membership.setEndDate(membershipRequest.endDate());
        membership.setPricePaid(membershipRequest.pricePaid());
        membership.setMembershipStatus(MembershipStatus.ACTIVE);
        membership.setCreatedAt(LocalDateTime.now());
        membership.setUpdatedAt(LocalDateTime.now());

        Membership response = membershipRepo.save(membership);


        MembershipResponse membershipResponse = getMembershipResponseFromMembership(response);


        Payment payment = new Payment();
        payment.setMembershipId(response.getMembershipId());
        payment.setPricePaid(membershipRequest.pricePaid());
        payment.setPaymentDateTime(membershipRequest.paymentDateTime());
        payment.setPaymentMethod(membershipRequest.paymentMethod());
        payment.setTransactionId(membershipRequest.transactionId());
        payment.setReceiptUrl("");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        return membershipResponse;
    }

    private static MembershipResponse getMembershipResponseFromMembership(Membership response) {
        return new MembershipResponse(
                response.getMembershipId(),
                response.getStartDate(),
                response.getEndDate(),
                response.getPricePaid(),
                response.getMembershipStatus()
        );
    }

    public MembershipDetailResponse getMembership(String memberId, String membershipId) {

        Optional<Membership> membershipOptional = membershipRepo.findById(membershipId);
        if (membershipOptional.isPresent()) {

            Membership membership = membershipOptional.get();
            MembershipResponse membershipResponse = getMembershipResponseFromMembership(membership);

            System.out.println(membershipResponse);

           List<Payment> payments = paymentRepo.getPaymentsByMembershipId(membershipId);

           List<PaymentResponse> paymentResponses = new java.util.ArrayList<>(List.of());

           payments.forEach(payment -> {
               PaymentResponse paymentResponse = new PaymentResponse(
                       payment.getPaymentId(),
                       payment.getPricePaid(),
                       payment.getPaymentDateTime(),
                       payment.getPaymentMethod(),
                       payment.getTransactionId(),
                       payment.getTransactionId()

               );

               paymentResponses.add(paymentResponse);
           });
           String planId = membership.getPlanId();
           Plan plan = new Plan();
           if (planId != null) {
               Optional<Plan> planOptional = planRepo.findById(planId);
               if (planOptional.isPresent()) {
                   plan = planOptional.get();
               }
           }

          PlanResponse planResponse = new PlanResponse(
                  planId,
                  plan.getPlanName(),
                  plan.getDuration_months(),
                  plan.getPrice(),
                  plan.getDescription()
          );


            MembershipDetailResponse membershipDetailResponse = new MembershipDetailResponse(
                  membershipResponse,
                    paymentResponses,
                    planResponse
            );

            return membershipDetailResponse;

        }

        throw new IllegalArgumentException("Input is invalid");
    }

    public List<MembershipResponse> getAllMemberships(String memberId) {

        List<Membership> memberships = membershipRepo.findMembershipByMemberId(memberId);

        List<MembershipResponse> membershipResponses = new java.util.ArrayList<>(List.of());

        memberships.forEach(membership -> {
            MembershipResponse membershipResponse = getMembershipResponseFromMembership(membership);
            membershipResponses.add(membershipResponse);
        });
        return membershipResponses;


    }
}
