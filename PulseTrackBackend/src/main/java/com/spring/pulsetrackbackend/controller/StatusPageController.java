package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.StatusPageRequest;
import com.spring.pulsetrackbackend.dto.StatusPageResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.model.StatusPage;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import com.spring.pulsetrackbackend.service.StatusPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/status-pages")
@RequiredArgsConstructor
public class StatusPageController {

    private final StatusPageService statusPageService;
    private final MonitorLogRepository monitorLogRepository;

    @PostMapping
    public ResponseEntity<StatusPageResponse> createStatusPage(@RequestBody StatusPageRequest request,
                                                               Principal principal) {
        System.out.println("🔐 Reached StatusPageController - User: " + principal.getName());
        return ResponseEntity.ok(statusPageService.createStatusPage(principal.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<StatusPageResponse>> getUserStatusPages(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        String email = principal.getName();
        return ResponseEntity.ok(statusPageService.getUserStatusPages(email));
    }

    @GetMapping("/public/{publicKey}")
    public ResponseEntity<StatusPageResponse> getPublicStatusPage(@PathVariable String publicKey) {
        return ResponseEntity.ok(statusPageService.getPublicStatusPage(publicKey));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatusPage(@PathVariable Long id, Principal principal) {
        String email = principal.getName(); // ✅ Extract user email from security context
        statusPageService.deleteStatusPage(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/{publicKey}/monitor/{monitorId}/logs")
    public ResponseEntity<List<Map<String, Object>>> getPublicMonitorLogs(
            @PathVariable String publicKey,
            @PathVariable Long monitorId) {

        StatusPage page = statusPageService.getStatusPageEntity(publicKey);
        Monitor monitor = page.getMonitors().stream()
                .filter(m -> m.getId().equals(monitorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Monitor not part of this status page"));

        List<MonitorLog> logs = monitorLogRepository.findTop50ByMonitorOrderByCheckedAtDesc(monitor);

        List<Map<String, Object>> response = logs.stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", log.getCheckedAt());
                    map.put("responseTime", log.getResponseTime());
                    map.put("statusCode", log.getStatusCode());
                    return map;
                })
                .toList();  // Java 16+; use `.collect(Collectors.toList())` if Java 11

        return ResponseEntity.ok(response);
    }



}