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
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import com.jatana.gymmembershipmanagemt.model.dto.response.ErrorResponse;
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
    public ResponseEntity<?> createMember(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
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
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            
    } catch (Exception e) {
        log.error("Internal error while creating member - ID: {}, email: {}. Error: {}", 
            memberRequest.memberId(), memberRequest.email(), e.getMessage(), e);
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/members")
    public ResponseEntity<?> getMembers(
        @RequestParam(required = false) String filter,
        @RequestParam(required = false, defaultValue = "") String searchKey,
        HttpServletRequest request) {
        
        log.info("Received request to fetch members - filter: {}, search key: '{}'", filter, searchKey);
        
        try {
            MemberStatus memberStatus;
            if (filter != null && !filter.isBlank()) {
                try {
                    memberStatus = MemberStatus.valueOf(filter.toUpperCase());
                    log.debug("Using filter status: {}", memberStatus);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid filter value provided: {}. Valid values are: ACTIVE, INACTIVE", filter);
                    ErrorResponse err = ErrorResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .message("Invalid filter value: " + filter)
                            .path(request.getRequestURI())
                            .build();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
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
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/membersByDate")
    public ResponseEntity<?> getMembersByDate(@RequestParam LocalDate endDate, HttpServletRequest request) {
        log.info("Received request to fetch members by end date: {}", endDate);
        
        try {
            List<MemberSummaryResponse> memberSummaryResponses = memberService.getMembersByDate(endDate);
            
            log.info("Successfully retrieved {} member(s) with membership ending on or before: {}", 
                    memberSummaryResponses.size(), endDate);
            
            return ResponseEntity.ok(memberSummaryResponses);
            
    } catch (Exception e) {
        log.error("Internal error while fetching members by date: {}. Error: {}", 
            endDate, e.getMessage(), e);
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/member")
    public ResponseEntity<?> getMember(@RequestParam String memberId, HttpServletRequest request) {
        log.info("Received request to fetch member details - ID: {}", memberId);
        
        try {
            MemberResponse memberResponse = memberService.getMember(memberId);
            
            log.info("Successfully retrieved member details - ID: {}, name: {}, status: {}", 
                    memberId, memberResponse.fullName(), memberResponse.memberStatus());
            
            return ResponseEntity.ok(memberResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Member not found with ID: {}. Error: {}", memberId, e.getMessage());
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(e.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while fetching member - ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Internal server error")
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PutMapping("/member")
    public ResponseEntity<?> updateMember(
        @RequestParam String memberId, 
        @RequestBody MemberUpdateRequest memberUpdateRequest,
        HttpServletRequest request) {
        
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
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while updating member - ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PatchMapping("/member")
    public ResponseEntity<?> updateMemberStatus(
        @RequestParam String memberId, 
        @RequestBody MemberStatus memberStatus,
        HttpServletRequest request) {
        
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
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while updating member status - ID: {}, target status: {}. Error: {}", 
                    memberId, memberStatus, e.getMessage(), e);
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}