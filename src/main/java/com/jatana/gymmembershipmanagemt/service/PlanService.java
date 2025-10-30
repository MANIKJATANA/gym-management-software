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

        Plan plan = getPlanFromPlanRequest(planRequest);
        Plan response = planRepo.save(plan);
        return gerPlanResponseFromPlan(response);


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
        return planRepo.findAll().stream().map(this::gerPlanResponseFromPlan).collect(Collectors.toList());
    }

    public PlanResponse getPlan(String planId) {
        Optional<Plan> plan = planRepo.findById(planId);
        if (plan.isPresent()) {
            return gerPlanResponseFromPlan(plan.get());
        }
        log.error("Plan not found");
        throw new IllegalArgumentException("Plan not found");
    }

    public void deletePlan(String planId) {
        try {
            planRepo.deleteById(planId);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Plan not found");
        }
    }

    public PlanResponse updatePlan(PlanRequest planRequest) {
        String planId = planRequest.planName();
        Optional<Plan> planOptional = planRepo.findById(planId);
        if(planOptional.isEmpty()){
            log.error("Plan not found");
            throw new IllegalArgumentException("Plan not found");
        }
        Plan plan = planOptional.get();
        plan.setDescription(planRequest.description());
        plan.setPrice(planRequest.price());
        plan.setDuration_months(planRequest.duration_months());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setDescription(planRequest.description());


        Plan response = planRepo.save(plan);
        return gerPlanResponseFromPlan(response);

    }
}
