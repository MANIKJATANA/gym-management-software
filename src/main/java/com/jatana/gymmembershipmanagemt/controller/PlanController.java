package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.PlanRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.PlanResponse;
import com.jatana.gymmembershipmanagemt.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class PlanController {

    @Autowired
    private PlanService planService;

    @PostMapping("/plan")
    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest planRequest) {
        log.info("Received request to create plan: {}", planRequest.planName());
        
        try {
            PlanResponse planResponse = planService.createPlan(planRequest);
            log.info("Successfully created plan with ID: {}", planResponse.planId());
            return new ResponseEntity<>(planResponse, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            log.error("Bad request while creating plan: {}. Error: {}", 
                    planRequest.planName(), e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            log.error("Internal error while creating plan: {}. Error: {}", 
                    planRequest.planName(), e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponse>> getPlans() {
        log.info("Received request to fetch all plans");
        
        try {
            List<PlanResponse> planResponses = planService.getPlans();
            log.info("Successfully retrieved {} plan(s)", planResponses.size());
            return new ResponseEntity<>(planResponses, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Internal error while fetching plans. Error: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/plan")
    public ResponseEntity<PlanResponse> getPlan(@RequestParam("planId") String planId) {
        log.info("Received request to fetch plan with ID: {}", planId);
        
        try {
            PlanResponse planResponse = planService.getPlan(planId);
            log.info("Successfully retrieved plan with ID: {}", planId);
            return new ResponseEntity<>(planResponse, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("Plan not found with ID: {}. Error: {}", planId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while fetching plan with ID: {}. Error: {}", 
                    planId, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/plan")
    public ResponseEntity<String> deletePlan(@RequestParam("planId") String planId) {
        log.info("Received request to delete plan with ID: {}", planId);
        
        try {
            planService.deletePlan(planId);
            log.info("Successfully deleted plan with ID: {}", planId);
            return new ResponseEntity<>("Plan deleted successfully", HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete - Plan not found with ID: {}. Error: {}", 
                    planId, e.getMessage());
            return new ResponseEntity<>("Plan not found with ID: " + planId, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while deleting plan with ID: {}. Error: {}", 
                    planId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to delete plan", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/plan")
    public ResponseEntity<PlanResponse> updatePlan(@RequestBody PlanRequest planRequest) {
        log.info("Received request to update plan: {}", planRequest.planName());
        
        try {
            PlanResponse planResponse = planService.updatePlan(planRequest);
            log.info("Successfully updated plan with ID: {}", planResponse.planId());
            return new ResponseEntity<>(planResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("Cannot update - Plan not found: {}. Error: {}", 
                    planRequest.planName(), e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            log.error("Internal error while updating plan: {}. Error: {}", 
                    planRequest.planName(), e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}