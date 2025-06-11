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
    public ResponseEntity<List<AlertResponse>> getAlerts(@RequestParam boolean resolved, Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(alertService.getAlerts(email, resolved));
    }

    @PutMapping("/{alertId}/resolve")
    public ResponseEntity<String> resolveAlert(
            @PathVariable Long alertId,
            Principal principal){
        String email = principal.getName();
        alertService.resolveAlert(alertId, email);
        return ResponseEntity.ok().build();
    }
}