package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MembershipRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipDetailResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipResponse;
import com.jatana.gymmembershipmanagemt.service.MembershipService;
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
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api")
@Tag(name = "Memberships", description = "APIs for managing gym memberships")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @Operation(
        summary = "Add a new membership",
        description = "Creates a new membership for a member with specified plan and payment details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Membership created successfully",
            content = @Content(schema = @Schema(implementation = MembershipResponse.class))
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
    @PostMapping("/membership")
    public ResponseEntity<?> addMembership(
            @Parameter(description = "ID of the member", required = true)
            @RequestParam String memberId, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Membership details including plan and payment information",
                required = true,
                content = @Content(schema = @Schema(implementation = MembershipRequest.class))
            )
            @RequestBody MembershipRequest membershipRequest,
            HttpServletRequest request) {
        
        log.info("Received request to create membership - member ID: {}, plan ID: {}, start date: {}, end date: {}", 
                memberId, membershipRequest.planId(), 
                membershipRequest.startDate(), membershipRequest.endDate());
        
        try {
            MembershipResponse membershipResponse = membershipService.addMembership(memberId, membershipRequest);
            
            log.info("Successfully created membership - membership ID: {}, member ID: {}, plan ID: {}, price paid: {}", 
                    membershipResponse.membershipId(), memberId,
                    membershipRequest.planId(), membershipRequest.pricePaid());
            
            return new ResponseEntity<>(membershipResponse, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request while creating membership for member ID: {}. Error: {}", 
                    memberId, e.getMessage());
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(e.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while creating membership for member ID: {}, plan ID: {}. Error: {}", 
                    memberId, membershipRequest.planId(), e.getMessage(), e);
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
        summary = "Get membership details",
        description = "Retrieves detailed information about a specific membership including payment history and plan details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved membership details",
            content = @Content(schema = @Schema(implementation = MembershipDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Membership not found or access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/membership")
    public ResponseEntity<?> getMembership(
            @Parameter(description = "ID of the member", required = true)
            @RequestParam String memberId, 
            @Parameter(description = "ID of the membership to retrieve", required = true)
            @RequestParam String membershipId,
            HttpServletRequest request) {
        
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
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(e.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while fetching membership - member ID: {}, membership ID: {}. Error: {}", 
                    memberId, membershipId, e.getMessage(), e);
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