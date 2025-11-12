package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.PlanRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.PlanResponse;
import com.jatana.gymmembershipmanagemt.service.PlanService;
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

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api")
@Tag(name = "Membership Plans", description = "APIs for managing gym membership plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Operation(
        summary = "Create a new membership plan",
        description = "Creates a new membership plan with specified duration and price"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Plan created successfully",
            content = @Content(schema = @Schema(implementation = PlanResponse.class))
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
    @PostMapping("/plan")
    public ResponseEntity<?> createPlan(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Plan details to create",
            required = true,
            content = @Content(schema = @Schema(implementation = PlanRequest.class))
        )
        @RequestBody PlanRequest planRequest,
        HttpServletRequest request) {
        log.info("Received request to create plan: {}", planRequest.planName());
        
        try {
            PlanResponse planResponse = planService.createPlan(planRequest);
            log.info("Successfully created plan with ID: {}", planResponse.planId());
            return new ResponseEntity<>(planResponse, HttpStatus.CREATED);

    } catch (IllegalArgumentException e) {
        log.error("Bad request while creating plan: {}. Error: {}", 
            planRequest.planName(), e.getMessage());
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            
    } catch (Exception e) {
        log.error("Internal error while creating plan: {}. Error: {}", 
            planRequest.planName(), e.getMessage(), e);
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
        summary = "Get all membership plans",
        description = "Retrieves all available membership plans"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved plans",
            content = @Content(schema = @Schema(implementation = PlanResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans(HttpServletRequest request) {
        log.info("Received request to fetch all plans");
        
        try {
            List<PlanResponse> planResponses = planService.getPlans();
            log.info("Successfully retrieved {} plan(s)", planResponses.size());
            return new ResponseEntity<>(planResponses, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Internal error while fetching plans. Error: {}", e.getMessage(), e);
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
        summary = "Get plan details",
        description = "Retrieves details of a specific membership plan"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved plan details",
            content = @Content(schema = @Schema(implementation = PlanResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Plan not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/plan")
    public ResponseEntity<?> getPlan(
        @Parameter(description = "ID of the plan to retrieve", required = true)
        @RequestParam("planId") String planId,
        HttpServletRequest request) {
        log.info("Received request to fetch plan with ID: {}", planId);
        
        try {
            PlanResponse planResponse = planService.getPlan(planId);
            log.info("Successfully retrieved plan with ID: {}", planId);
            return new ResponseEntity<>(planResponse, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("Plan not found with ID: {}. Error: {}", planId, e.getMessage());
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(e.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
        } catch (Exception e) {
            log.error("Internal error while fetching plan with ID: {}. Error: {}", 
                    planId, e.getMessage(), e);
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
        summary = "Delete a membership plan",
        description = "Deletes a specific membership plan"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Plan not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/plan")
    public ResponseEntity<?> deletePlan(
        @Parameter(description = "ID of the plan to delete", required = true)
        @RequestParam("planId") String planId,
        HttpServletRequest request) {
        log.info("Received request to delete plan with ID: {}", planId);
        
        try {
            planService.deletePlan(planId);
            log.info("Successfully deleted plan with ID: {}", planId);
            return new ResponseEntity<>("Plan deleted successfully", HttpStatus.OK);
            
    } catch (IllegalArgumentException e) {
        log.error("Cannot delete - Plan not found with ID: {}. Error: {}", 
            planId, e.getMessage());
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message("Plan not found with ID: " + planId)
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
    } catch (Exception e) {
        log.error("Internal error while deleting plan with ID: {}. Error: {}", 
            planId, e.getMessage(), e);
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Failed to delete plan")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @Operation(
        summary = "Update a membership plan",
        description = "Updates details of an existing membership plan"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan updated successfully",
            content = @Content(schema = @Schema(implementation = PlanResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Plan not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/plan")
    public ResponseEntity<?> updatePlan(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated plan details",
            required = true,
            content = @Content(schema = @Schema(implementation = PlanRequest.class))
        )
        @RequestBody PlanRequest planRequest,
        HttpServletRequest request) {
        log.info("Received request to update plan: {}", planRequest.planName());
        
        try {
            PlanResponse planResponse = planService.updatePlan(planRequest);
            log.info("Successfully updated plan with ID: {}", planResponse.planId());
            return new ResponseEntity<>(planResponse, HttpStatus.OK);

    } catch (IllegalArgumentException e) {
        log.error("Cannot update - Plan not found: {}. Error: {}", 
            planRequest.planName(), e.getMessage());
        ErrorResponse err = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            
    } catch (Exception e) {
        log.error("Internal error while updating plan: {}. Error: {}", 
            planRequest.planName(), e.getMessage(), e);
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