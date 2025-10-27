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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest){
        try{
            MemberResponse memberResponse = memberService.createMember(memberRequest);
            return ResponseEntity.ok(memberResponse);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberSummaryResponse>> getMembers(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "") String searchKey) {
        try {
            MemberStatus memberStatus;
            if (filter != null && !filter.isBlank()) {
                try {
                    memberStatus = MemberStatus.valueOf(filter.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid filter value: {}", filter);
                    throw e;
                }
            }else{
                memberStatus = MemberStatus.ACTIVE;
            }
            List<MemberSummaryResponse> memberSummaryResponses = memberService.getMembers(memberStatus, searchKey);
            return ResponseEntity.ok(memberSummaryResponses);
        }catch (Exception e) {
            log.error("error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getMember(@RequestParam String memberId){
        try {
            MemberResponse memberResponse = memberService.getMember(memberId);
            return ResponseEntity.ok(memberResponse);
        }catch (Exception e){
            log.error("error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/member")
    public ResponseEntity<MemberResponse> updateMember(@RequestParam String memberId,@RequestBody MemberUpdateRequest memberUpdateRequest){
        try{
            MemberResponse memberResponse = memberService.updateMember(memberUpdateRequest,memberId);
            return ResponseEntity.ok(memberResponse);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/member")
    public ResponseEntity<MemberResponse> updateMemberStatus(@RequestParam String memberId,@RequestBody MemberStatus memberStatus){
        try{
            MemberResponse memberResponse = memberService.updateMemberStatus(memberId, memberStatus);
            return ResponseEntity.ok(memberResponse);
        }
        catch(Exception e){
            log.error("error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
