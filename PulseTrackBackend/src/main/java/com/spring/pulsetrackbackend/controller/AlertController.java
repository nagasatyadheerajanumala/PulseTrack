package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.AlertResponse;
import com.spring.pulsetrackbackend.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(alertService.getUserAlerts(email));
    }
}