package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MemberRequest;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberUpdateRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberSummaryResponse;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import com.jatana.gymmembershipmanagemt.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member Management", description = "APIs for managing gym members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Operation(
        summary = "Create a new member",
        description = "Creates a new member in the system with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Member created successfully",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/member")
    public ResponseEntity<?> createMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Member details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = MemberRequest.class))
            )
            @RequestBody MemberRequest memberRequest,
            HttpServletRequest request) {
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

    @Operation(
        summary = "Get all members",
        description = "Retrieves all members matching the specified filter and search criteria"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved members",
            content = @Content(schema = @Schema(implementation = MemberSummaryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid filter value",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/members")
    public ResponseEntity<?> getMembers(
        @Parameter(description = "Filter members by status (ACTIVE/INACTIVE)", example = "ACTIVE")
        @RequestParam(required = false) String filter,
        @Parameter(description = "Search key for member name, email, or ID")
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

    @Operation(
        summary = "Get members by membership end date",
        description = "Retrieves all active members whose membership ends on or before the specified date"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved members",
            content = @Content(schema = @Schema(implementation = MemberSummaryResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/membersByDate")
    public ResponseEntity<?> getMembersByDate(
        @Parameter(description = "Target end date (YYYY-MM-DD)", example = "2024-12-31", required = true)
        @RequestParam LocalDate endDate,
        HttpServletRequest request) {
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

    @Operation(
        summary = "Get member details",
        description = "Retrieves detailed information about a specific member including membership history and documents"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved member details",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Member not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/member")
    public ResponseEntity<?> getMember(
        @Parameter(description = "ID of the member to retrieve", required = true)
        @RequestParam String memberId,
        HttpServletRequest request) {
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

    @Operation(
        summary = "Update member details",
        description = "Updates the information of an existing member"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Member updated successfully",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Member not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/member")
    public ResponseEntity<?> updateMember(
        @Parameter(description = "ID of the member to update", required = true)
        @RequestParam String memberId, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated member details",
            required = true,
            content = @Content(schema = @Schema(implementation = MemberUpdateRequest.class))
        )
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

    @Operation(
        summary = "Update member status",
        description = "Updates the status of a member (ACTIVE/INACTIVE)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Member status updated successfully",
            content = @Content(schema = @Schema(implementation = MemberResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Member not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/member")
    public ResponseEntity<?> updateMemberStatus(
        @Parameter(description = "ID of the member to update", required = true)
        @RequestParam String memberId, 
        @Parameter(description = "New status of the member", required = true, schema = @Schema(implementation = MemberStatus.class))
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