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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MembershipService {

    @Autowired
    private MembershipRepo membershipRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PlanRepo planRepo;

    @Transactional
    public MembershipResponse addMembership(String memberId, MembershipRequest membershipRequest) {
        log.info("Adding new membership for member ID: {} with plan ID: {}", 
                memberId, membershipRequest.planId());
        
        try {
            
            
            log.debug("Creating membership entity for member ID: {}, start date: {}, end date: {}", 
                    memberId, membershipRequest.startDate(), membershipRequest.endDate());
            
            Membership membership = new Membership();
            membership.setMemberId(memberId);
            membership.setPlanId(membershipRequest.planId());
            membership.setStartDate(membershipRequest.startDate());
            membership.setEndDate(membershipRequest.endDate());
            membership.setPricePaid(membershipRequest.pricePaid());
            membership.setMembershipStatus(MembershipStatus.ACTIVE);
            membership.setCreatedAt(LocalDateTime.now());
            membership.setUpdatedAt(LocalDateTime.now());

            Membership savedMembership = membershipRepo.save(membership);
            log.info("Successfully created membership with ID: {} for member ID: {}", 
                    savedMembership.getMembershipId(), memberId);

            MembershipResponse membershipResponse = getMembershipResponseFromMembership(savedMembership);

            log.debug("Creating payment record for membership ID: {}, amount: {}", 
                    savedMembership.getMembershipId(), membershipRequest.pricePaid());
            
            Payment payment = new Payment();
            payment.setMembershipId(savedMembership.getMembershipId());
            payment.setPricePaid(membershipRequest.pricePaid());
            payment.setPaymentDateTime(membershipRequest.paymentDateTime());
            payment.setPaymentMethod(membershipRequest.paymentMethod());
            payment.setTransactionId(membershipRequest.transactionId());
            payment.setReceiptUrl("");
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment = paymentRepo.save(payment);
            log.info("Successfully created payment record with ID: {} for membership ID: {}", 
                    savedPayment.getPaymentId(), savedMembership.getMembershipId());

            return membershipResponse;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create membership for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw new RuntimeException("Failed to create membership for member ID: " + memberId, e);
        }
    }

    private static MembershipResponse getMembershipResponseFromMembership(Membership membership) {
        return new MembershipResponse(
                membership.getMembershipId(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getPricePaid(),
                membership.getMembershipStatus()
        );
    }

    public MembershipDetailResponse getMembership(String memberId, String membershipId) {
        log.info("Fetching membership details - member ID: {}, membership ID: {}", 
                memberId, membershipId);

        try {
            Optional<Membership> membershipOptional = membershipRepo.findById(membershipId);
            
            if (membershipOptional.isEmpty()) {
                log.error("Membership not found - member ID: {}, membership ID: {}", 
                        memberId, membershipId);
                throw new IllegalArgumentException("Membership not found with ID: " + membershipId);
            }

            Membership membership = membershipOptional.get();
            
            // Verify membership belongs to the member
            if (!membership.getMemberId().equals(memberId)) {
                log.warn("Membership ID: {} does not belong to member ID: {}", 
                        membershipId, memberId);
                throw new IllegalArgumentException("Membership does not belong to the specified member");
            }
            
            log.debug("Found membership - ID: {}, status: {}", 
                    membershipId, membership.getMembershipStatus());
            
            MembershipResponse membershipResponse = getMembershipResponseFromMembership(membership);

            // Fetch payments
            log.debug("Fetching payments for membership ID: {}", membershipId);
            List<Payment> payments = paymentRepo.getPaymentsByMembershipId(membershipId);
            log.debug("Found {} payment(s) for membership ID: {}", payments.size(), membershipId);

            List<PaymentResponse> paymentResponses = payments.stream()
                    .map(payment -> new PaymentResponse(
                            payment.getPaymentId(),
                            payment.getPricePaid(),
                            payment.getPaymentDateTime(),
                            payment.getPaymentMethod(),
                            payment.getTransactionId(),
                            payment.getReceiptUrl() // Fixed: was using transactionId twice
                    ))
                    .collect(Collectors.toList());

            // Fetch plan details
            String planId = membership.getPlanId();
            PlanResponse planResponse;
            
            if (planId != null) {
                log.debug("Fetching plan details for plan ID: {}", planId);
                Optional<Plan> planOptional = planRepo.findById(planId);
                
                if (planOptional.isPresent()) {
                    Plan plan = planOptional.get();
                    planResponse = new PlanResponse(
                            plan.getPlanId(),
                                    plan.getPlanName(),
                                    plan.getDurationMonths(),
                                    plan.getPrice(),
                                    plan.getDescription()
                    );
                    log.debug("Successfully retrieved plan details for plan ID: {}", planId);
                } else {
                    log.warn("Plan not found with ID: {} for membership ID: {}", 
                            planId, membershipId);
                    planResponse = new PlanResponse(planId, null, 0, 0, "Plan details not available");
                }
            } else {
                log.warn("No plan ID associated with membership ID: {}", membershipId);
                planResponse = new PlanResponse(null, null, 0, 0, "No plan associated");
            }

            log.info("Successfully retrieved membership details for member ID: {}, membership ID: {}", 
                    memberId, membershipId);
            
            return new MembershipDetailResponse(
                    membershipResponse,
                    paymentResponses,
                    planResponse
            );

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch membership details - member ID: {}, membership ID: {}. Error: {}", 
                    memberId, membershipId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch membership details", e);
        }
    }

    public List<MembershipResponse> getAllMemberships(String memberId) {
        log.info("Fetching all memberships for member ID: {}", memberId);

        try {
            List<Membership> memberships = membershipRepo.findMembershipByMemberId(memberId);
            log.debug("Found {} membership(s) for member ID: {}", memberships.size(), memberId);

            List<MembershipResponse> membershipResponses = memberships.stream()
                    .map(MembershipService::getMembershipResponseFromMembership)
                    .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} membership(s) for member ID: {}", 
                    membershipResponses.size(), memberId);
            
            return membershipResponses;
            
        } catch (Exception e) {
            log.error("Failed to fetch memberships for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch memberships for member ID: " + memberId, e);
        }
    }
}