package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.MonitorRequest;
import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.service.MonitorService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> toggleMonitor(
            @PathVariable Long monitorId,
            Principal principal) {
        boolean updatedStatus = monitorService.toggleMonitorStatus(monitorId, principal.getName());
        Map<String, Object> result = new HashMap<>();
        result.put("active", updatedStatus);
        return ResponseEntity.ok(result);
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
    @PutMapping("/{monitorId}")
    public ResponseEntity<MonitorResponse> updateMonitor(
            @PathVariable Long monitorId,
            @RequestBody MonitorRequest request,
            Principal principal) {

        String email = principal.getName();
        MonitorResponse updated = monitorService.updateMonitor(monitorId, email, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitorResponse> getMonitorById(
            @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(monitorService.getMonitorById(id, email));
    }

}
