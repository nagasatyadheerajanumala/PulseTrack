package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.MonitorLogResponse;
import com.spring.pulsetrackbackend.model.Alert;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.repository.AlertRepository;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorLogService {

    private final MonitorLogRepository monitorLogRepo;
    private final MonitorRepository monitorRepo;
    private final AlertRepository alertRepo;

    public void saveLog(Long monitorId, int statusCode, long responseTime) {
        Monitor monitor = monitorRepo.findById(monitorId)
                .orElseThrow(() -> new IllegalArgumentException("Monitor not found"));

        MonitorLog log = MonitorLog.builder()
                .monitor(monitor)
                .statusCode(statusCode)
                .responseTime(responseTime)
                .checkedAt(LocalDateTime.now())
                .build();

        monitorLogRepo.save(log);

        if (statusCode != 200) {
            Alert alert = Alert.builder()
                    .monitor(monitor)
                    .message("Monitor \"" + monitor.getName() + "\" is down. Status code: " + statusCode)
                    .createdAt(LocalDateTime.now())
                    .resolved(false)
                    .build();

            alertRepo.save(alert);
        }
    }

    public List<MonitorLogResponse> getLogsForMonitor(Long monitorId, String userEmail) {
        Monitor monitor = monitorRepo.findById(monitorId)
                .filter(m -> m.getUser().getEmail().equals(userEmail))
                .orElseThrow(() -> new RuntimeException("Monitor not found or access denied"));

        return monitorLogRepo.findByMonitor(monitor).stream()
                .map(log -> MonitorLogResponse.builder()
                        .id(log.getId())
                        .statusCode(log.getStatusCode())
                        .responseTime(log.getResponseTime())
                        .checkedAt(log.getCheckedAt())
                        .build())
                .collect(Collectors.toList());
    }
}