package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MemberRequest;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberUpdateRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberSummaryResponse;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import com.jatana.gymmembershipmanagemt.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest) {
        log.info("Received request to create member - ID: {}, name: {} {}, email: {}", 
                memberRequest.memberId(), 
                memberRequest.firstName(), 
                memberRequest.lastName(), 
                memberRequest.email());
        
        try {
            MemberResponse memberResponse = memberService.createMember(memberRequest);
            
            log.info("Successfully created member - ID: {}, name: {}, status: {}", 
                    memberResponse.memberId(), 
                    memberResponse.fullName(), 
                    memberResponse.memberStatus());
            
            return new ResponseEntity<>(memberResponse, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request while creating member - ID: {}, email: {}. Error: {}", 
                    memberRequest.memberId(), memberRequest.email(), e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            log.error("Internal error while creating member - ID: {}, email: {}. Error: {}", 
                    memberRequest.memberId(), memberRequest.email(), e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberSummaryResponse>> getMembers(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "") String searchKey) {
        
        log.info("Received request to fetch members - filter: {}, search key: '{}'", filter, searchKey);
        
        try {
            MemberStatus memberStatus;
            if (filter != null && !filter.isBlank()) {
                try {
                    memberStatus = MemberStatus.valueOf(filter.toUpperCase());
                    log.debug("Using filter status: {}", memberStatus);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid filter value provided: {}. Valid values are: ACTIVE, INACTIVE", filter);
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            } else {
                memberStatus = MemberStatus.ACTIVE;
                log.debug("No filter provided, defaulting to ACTIVE status");
            }
            
            List<MemberSummaryResponse> memberSummaryResponses = 
                    memberService.getMembers(memberStatus, searchKey);
            
            log.info("Successfully retrieved {} member(s) with filter: {}, search key: '{}'", 
                    memberSummaryResponses.size(), memberStatus, searchKey);
            
            return ResponseEntity.ok(memberSummaryResponses);
            
        } catch (Exception e) {
            log.error("Internal error while fetching members - filter: {}, search key: '{}'. Error: {}", 
                    filter, searchKey, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/membersByDate")
    public ResponseEntity<List<MemberSummaryResponse>> getMembersByDate(@RequestParam LocalDate endDate) {
        log.info("Received request to fetch members by end date: {}", endDate);
        
        try {
            List<MemberSummaryResponse> memberSummaryResponses = memberService.getMembersByDate(endDate);
            
            log.info("Successfully retrieved {} member(s) with membership ending on or before: {}", 
                    memberSummaryResponses.size(), endDate);
            
            return ResponseEntity.ok(memberSummaryResponses);
            
        } catch (Exception e) {
            log.error("Internal error while fetching members by date: {}. Error: {}", 
                    endDate, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getMember(@RequestParam String memberId) {
        log.info("Received request to fetch member details - ID: {}", memberId);
        
        try {
            MemberResponse memberResponse = memberService.getMember(memberId);
            
            log.info("Successfully retrieved member details - ID: {}, name: {}, status: {}", 
                    memberId, memberResponse.fullName(), memberResponse.memberStatus());
            
            return ResponseEntity.ok(memberResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Member not found with ID: {}. Error: {}", memberId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while fetching member - ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/member")
    public ResponseEntity<MemberResponse> updateMember(
            @RequestParam String memberId, 
            @RequestBody MemberUpdateRequest memberUpdateRequest) {
        
        log.info("Received request to update member - ID: {}, new name: {} {}", 
                memberId, 
                memberUpdateRequest.firstName(), 
                memberUpdateRequest.lastName());
        
        try {
            MemberResponse memberResponse = memberService.updateMember(memberUpdateRequest, memberId);
            
            log.info("Successfully updated member - ID: {}, name: {}, email: {}", 
                    memberId, memberResponse.fullName(), memberResponse.email());
            
            return ResponseEntity.ok(memberResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Cannot update - Member not found with ID: {}. Error: {}", 
                    memberId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while updating member - ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/member")
    public ResponseEntity<MemberResponse> updateMemberStatus(
            @RequestParam String memberId, 
            @RequestBody MemberStatus memberStatus) {
        
        log.info("Received request to update member status - ID: {}, new status: {}", 
                memberId, memberStatus);
        
        try {
            MemberResponse memberResponse = memberService.updateMemberStatus(memberId, memberStatus);
            
            log.info("Successfully updated member status - ID: {}, new status: {}, name: {}", 
                    memberId, memberStatus, memberResponse.fullName());
            
            return ResponseEntity.ok(memberResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Cannot update status - Member not found with ID: {}. Error: {}", 
                    memberId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while updating member status - ID: {}, target status: {}. Error: {}", 
                    memberId, memberStatus, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}