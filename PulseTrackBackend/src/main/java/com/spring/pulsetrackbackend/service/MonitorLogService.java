package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.MonitorLogResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorLogService {
    private final MonitorLogRepository logRepo;
    private final MonitorRepository monitorRepo;

    public List<MonitorLogResponse> getLogsForMonitor(Long monitorId, String userEmail) {
        Monitor monitor = monitorRepo.findById(monitorId)
                .filter(m -> m.getUser().getEmail().equals(userEmail))
                .orElseThrow(() -> new RuntimeException("Monitor not found or access denied"));

        return logRepo.findByMonitor(monitor).stream()
                .map(log -> MonitorLogResponse.builder()
                        .id(log.getId())
                        .statusCode(log.getStatusCode())
                        .responseTime(log.getResponseTime())
                        .checkedAt(log.getCheckedAt())
                        .build())
                .collect(Collectors.toList());
    }
}