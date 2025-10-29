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

    // create new membership
    @PostMapping("/membership")
    public ResponseEntity<MembershipResponse> addMembership(@RequestParam String memberId, @RequestBody MembershipRequest membershipRequest) {
        try{

            MembershipResponse membershipResponse = membershipService.addMembership(memberId,membershipRequest);
            return ResponseEntity.ok(membershipResponse);
        }catch (Exception e){
            log.error("Error {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // get a membership detail
    @GetMapping("/membership")
    public ResponseEntity<MembershipDetailResponse> getMembership(@RequestParam String memberId, @RequestParam String membershipId) {
        try{
            MembershipDetailResponse membershipResponseWithPayment = membershipService.getMembership(memberId, membershipId);
            return ResponseEntity.ok(membershipResponseWithPayment);
        } catch (Exception e) {
            log.error("Error {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
