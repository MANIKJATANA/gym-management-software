package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.Plan;
import com.jatana.gymmembershipmanagemt.model.dto.request.PlanRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.PlanResponse;
import com.jatana.gymmembershipmanagemt.repo.PlanRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlanService {
    
    @Autowired
    private PlanRepo planRepo;
    
    public PlanResponse createPlan(PlanRequest planRequest) {
        log.info("Creating new plan with name: {}", planRequest.planName());
        String planId = planRequest.planName();

        // Prevent creating a plan that already exists (planName used as ID)
        if (planRepo.existsById(planId)) {
            log.warn("Attempted to create a plan that already exists with ID/name: {}", planId);
            throw new IllegalArgumentException("Plan already exists with ID/name: " + planId);
        }

        try {
            Plan plan = getPlanFromPlanRequest(planRequest);
            Plan response = planRepo.save(plan);

            log.info("Successfully created plan with ID: {} and name: {}", 
                    response.getPlanId(), response.getPlanName());

            return gerPlanResponseFromPlan(response);
        } catch (Exception e) {
            log.error("Failed to create plan with name: {}. Error: {}", 
                    planRequest.planName(), e.getMessage(), e);
            throw e;
        }
    }

    private PlanResponse gerPlanResponseFromPlan(Plan plan) {
        return new PlanResponse(
                plan.getPlanId(),
                plan.getPlanName(),
                plan.getDuration_months(),
                plan.getPrice(),
                plan.getDescription()
        );
    }

    private Plan getPlanFromPlanRequest(PlanRequest planRequest) {
        log.debug("Mapping PlanRequest to Plan entity for: {}", planRequest.planName());
        
        Plan plan = new Plan();
        plan.setPlanId(planRequest.planName());
        plan.setPlanName(planRequest.planName());
        plan.setDescription(planRequest.description());
        plan.setPrice(planRequest.price());
        plan.setDuration_months(planRequest.duration_months());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setDescription(planRequest.description());
        
        return plan;
    }

    public List<PlanResponse> getPlans() {
        log.info("Fetching all plans");
        
        try {
            List<PlanResponse> plans = planRepo.findAll().stream()
                    .map(this::gerPlanResponseFromPlan)
                    .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} plan(s)", plans.size());
            return plans;
        } catch (Exception e) {
            log.error("Failed to fetch plans. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public PlanResponse getPlan(String planId) {
        log.info("Fetching plan with ID: {}", planId);
        
        Optional<Plan> plan = planRepo.findById(planId);
        
        if (plan.isPresent()) {
            log.info("Successfully found plan with ID: {}", planId);
            return gerPlanResponseFromPlan(plan.get());
        }
        
        log.error("Plan not found with ID: {}", planId);
        throw new IllegalArgumentException("Plan not found with ID: " + planId);
    }

    public void deletePlan(String planId) {
        log.info("Attempting to delete plan with ID: {}", planId);
        
        try {
            // Check if plan exists before deletion
            if (!planRepo.existsById(planId)) {
                log.warn("Attempted to delete non-existent plan with ID: {}", planId);
                throw new IllegalArgumentException("Plan not found with ID: " + planId);
            }
            
            planRepo.deleteById(planId);
            log.info("Successfully deleted plan with ID: {}", planId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete plan with ID: {}. Error: {}", planId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete plan with ID: " + planId, e);
        }
    }

    public PlanResponse updatePlan(PlanRequest planRequest) {
        String planId = planRequest.planName();
        log.info("Updating plan with ID: {}", planId);
        
        Optional<Plan> planOptional = planRepo.findById(planId);
        
        if (planOptional.isEmpty()) {
            log.error("Cannot update - Plan not found with ID: {}", planId);
            throw new IllegalArgumentException("Plan not found with ID: " + planId);
        }
        
        try {
            Plan plan = planOptional.get();
            
            log.debug("Updating plan details - ID: {}, Price: {}, Duration: {} months", 
                    planId, planRequest.price(), planRequest.duration_months());
            
            plan.setDescription(planRequest.description());
            plan.setPrice(planRequest.price());
            plan.setDuration_months(planRequest.duration_months());
            plan.setUpdatedAt(LocalDateTime.now());
            
            Plan response = planRepo.save(plan);
            
            log.info("Successfully updated plan with ID: {}", planId);
            
            return gerPlanResponseFromPlan(response);
        } catch (Exception e) {
            log.error("Failed to update plan with ID: {}. Error: {}", planId, e.getMessage(), e);
            throw e;
        }
    }
}