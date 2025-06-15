package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.MonitorRequest;
import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.service.MonitorService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {
    private final MonitorService monitorService;
    private Logger logger =  Logger.getLogger(MonitorController.class.getName());

    @PostMapping
    public ResponseEntity<MonitorResponse> createMonitor(@RequestBody MonitorRequest request, Principal principal){
        String email = principal.getName();
        return ResponseEntity.ok(monitorService.createMonitor(email, request));
    }

    @GetMapping
    public ResponseEntity<List<MonitorResponse>> getMonitors(Principal principal){
        String email = principal.getName();
        logger.info(email);
        return ResponseEntity.ok(monitorService.getUserMonitors(email));
    }

    @PutMapping("/{monitorId}/toggle")
    public ResponseEntity<Void> toggleMonitor(
            @PathVariable Long monitorId,
            Principal principal) {
        monitorService.toggleMonitorStatus(monitorId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMonitor(@PathVariable Long id, Principal principal) {
        monitorService.deleteMonitor(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<Map<String, Object>> getMonitorAnalytics(
            @PathVariable Long id,
            @RequestParam(defaultValue = "24h") String range,
            Principal principal) {

        String email = principal.getName();
        Map<String, Object> data = monitorService.getMonitorAnalytics(id, email, range);
        return ResponseEntity.ok(data);
    }
}
