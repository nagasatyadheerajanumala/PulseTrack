package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.MonitorLogResponse;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/monitors/{monitorId}/logs")
@RequiredArgsConstructor
public class MonitorLogController {
    private final MonitorLogService logService;

    @GetMapping
    public ResponseEntity<List<MonitorLogResponse>> getLogs(
            @PathVariable Long monitorId,
            Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(logService.getLogsForMonitor(monitorId, email));
    }
}