package com.jatana.gymmembershipmanagemt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin
public class TestController {
    @GetMapping("/status")
    public String status() {
        return "OK";
    }
}
