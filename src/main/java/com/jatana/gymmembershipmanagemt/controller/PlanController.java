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
        try{
            PlanResponse planResponse =  planService.createPlan(planRequest);
            return new ResponseEntity<>(planResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponse>> getPlans() {

        try {
            List<PlanResponse> planResponses = planService.getPlans();
            return new ResponseEntity<>(planResponses, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/plan")
    public ResponseEntity<PlanResponse> getPlan(@RequestParam("planId") String planId) {
        try{
            PlanResponse planResponse = planService.getPlan(planId);
            return new ResponseEntity<>(planResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/plan")
    public ResponseEntity<String> deletePlan(@RequestParam("planId") String planId) {
        try {
            planService.deletePlan(planId);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/plan")
    public ResponseEntity<PlanResponse> updatePlan(@RequestBody PlanRequest planRequest) {
        try{
            PlanResponse planResponse = planService.updatePlan(planRequest);
            return new ResponseEntity<>(planResponse, HttpStatus.OK);

        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
