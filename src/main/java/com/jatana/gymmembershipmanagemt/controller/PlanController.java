package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.PlanRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.PlanResponse;
import com.jatana.gymmembershipmanagemt.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import com.jatana.gymmembershipmanagemt.model.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class PlanController {

    @Autowired
    private PlanService planService;

    @PostMapping("/plan")
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest planRequest, HttpServletRequest request) {
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

    @GetMapping("/plan")
    public ResponseEntity<?> getPlan(@RequestParam("planId") String planId, HttpServletRequest request) {
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

    @DeleteMapping("/plan")
    public ResponseEntity<?> deletePlan(@RequestParam("planId") String planId, HttpServletRequest request) {
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

    @PutMapping("/plan")
    public ResponseEntity<?> updatePlan(@RequestBody PlanRequest planRequest, HttpServletRequest request) {
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