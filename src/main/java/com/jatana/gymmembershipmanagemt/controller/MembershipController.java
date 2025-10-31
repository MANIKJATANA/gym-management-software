package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MembershipRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipDetailResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipResponse;
import com.jatana.gymmembershipmanagemt.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/membership")
    public ResponseEntity<MembershipResponse> addMembership(
            @RequestParam String memberId, 
            @RequestBody MembershipRequest membershipRequest) {
        
        log.info("Received request to create membership - member ID: {}, plan ID: {}, start date: {}, end date: {}", 
                memberId, membershipRequest.planId(), 
                membershipRequest.startDate(), membershipRequest.endDate());
        
        try {
            MembershipResponse membershipResponse = membershipService.addMembership(memberId, membershipRequest);
            
            log.info("Successfully created membership - membership ID: {}, member ID: {}, plan ID: {}, price paid: {}", 
                    membershipResponse.memberShipId(), memberId,
                    membershipRequest.planId(), membershipRequest.pricePaid());
            
            return new ResponseEntity<>(membershipResponse, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request while creating membership for member ID: {}. Error: {}", 
                    memberId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            log.error("Internal error while creating membership for member ID: {}, plan ID: {}. Error: {}", 
                    memberId, membershipRequest.planId(), e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/membership")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @RequestParam String memberId, 
            @RequestParam String membershipId) {
        
        log.info("Received request to fetch membership details - member ID: {}, membership ID: {}", 
                memberId, membershipId);
        
        try {
            MembershipDetailResponse membershipDetailResponse = 
                    membershipService.getMembership(memberId, membershipId);
            
            log.info("Successfully retrieved membership details - member ID: {}, membership ID: {}, status: {}", 
                    memberId, membershipId, 
                    membershipDetailResponse.membershipResponse().membershipStatus());
            
            return ResponseEntity.ok(membershipDetailResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Membership not found or access denied - member ID: {}, membership ID: {}. Error: {}", 
                    memberId, membershipId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while fetching membership - member ID: {}, membership ID: {}. Error: {}", 
                    memberId, membershipId, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}