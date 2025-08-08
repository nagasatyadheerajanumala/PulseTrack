package com.spring.pulsetrackbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.pulsetrackbackend.dto.MonitorRequest;
import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.model.User;
import com.spring.pulsetrackbackend.repository.AlertRepository;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final UserRepository userRepo;
    private final MonitorLogRepository monitorLogRepository;
    private final AlertRepository alertRepository;
    private final MonitorSchedulerService monitorSchedulerService;

    private final ObjectMapper objectMapper;

    public MonitorResponse createMonitor(String userEmail, MonitorRequest request) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();

        String headersJson = null;
        try {
            if (request.getHeaders() != null) {
                headersJson = objectMapper.writeValueAsString(request.getHeaders());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize headers", e);
        }

        Monitor monitor = Monitor.builder()
                .name(request.getName())
                .url(request.getUrl())
                .checkFreq(request.getCheckFreq())
                .alertFrequencyMinutes(request.getAlertFrequencyMinutes())
                .httpMethod(request.getHttpMethod())
                .headersJson(headersJson)
                .requestBody(request.getRequestBody())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        monitorRepository.save(monitor);
        monitorSchedulerService.scheduleMonitor(monitor);

        return MonitorResponse.builder()
                .id(monitor.getId())
                .name(monitor.getName())
                .url(monitor.getUrl())
                .checkFreq(monitor.getCheckFreq())
                .isActive(monitor.isActive())
                .build();
    }

    public List<MonitorResponse> getUserMonitors(String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        return monitorRepository.findByUser(user).stream()
                .map(m -> MonitorResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .url(m.getUrl())
                        .checkFreq(m.getCheckFreq())
                        .isActive(m.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean toggleMonitorStatus(Long monitorId, String userEmail) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        boolean wasActive = monitor.isActive();
        monitor.setActive(!wasActive);
        monitorRepository.save(monitor);

        if (wasActive) {
            monitorSchedulerService.cancelMonitor(monitor.getId());
        } else {
            monitorSchedulerService.scheduleMonitor(monitor);
        }

        return !wasActive;
    }

    @Transactional
    public void deleteMonitor(Long monitorId, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        monitorSchedulerService.cancelMonitor(monitorId);

        if (monitor.getStatusPages() != null) {
            monitor.getStatusPages().forEach(sp -> sp.getMonitors().remove(monitor));
            monitor.getStatusPages().clear();
        }

        alertRepository.deleteByMonitor(monitor);
        monitorLogRepository.deleteByMonitor(monitor);
        monitorRepository.delete(monitor);
    }

    public Map<String, Object> getMonitorAnalytics(Long monitorId, String email, String range) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", monitor.getId());
        result.put("name", monitor.getName());
        result.put("url", monitor.getUrl());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since;
        switch (range) {
            case "1h": since = now.minusHours(1); break;
            case "7d": since = now.minusDays(7); break;
            case "30d": since = now.minusDays(30); break;
            case "24h":
            default: since = now.minusHours(24); break;
        }

        result.put("uptimePercent", calculateUptime(monitor, since));

        List<MonitorLog> logs = monitorLogRepository.findByMonitor(monitor);
        long avgResp = logs.isEmpty() ? 0 :
                logs.stream().mapToLong(MonitorLog::getResponseTime).sum() / logs.size();
        result.put("averageResponseTime", avgResp);

        MonitorLog lastLog = monitorLogRepository.findTopByMonitorOrderByCheckedAtDesc(monitor);
        result.put("lastChecked", lastLog != null ? lastLog.getCheckedAt() : null);
        result.put("lastStatusCode", lastLog != null ? lastLog.getStatusCode() : null);

        List<Map<String, Object>> recent = monitorLogRepository.findTop10ByMonitorOrderByCheckedAtDesc(monitor)
                .stream().map(log -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("timestamp", log.getCheckedAt());
                    m.put("statusCode", log.getStatusCode());
                    m.put("responseTime", log.getResponseTime());
                    return m;
                }).collect(Collectors.toList());

        result.put("recentChecks", recent);
        return result;
    }

    private Double calculateUptime(Monitor monitor, LocalDateTime since) {
        Object[] stats = monitorLogRepository.getUptimeStats(monitor, since);
        long total = stats.length > 0 && stats[0] instanceof Long ? (Long) stats[0] : 0L;
        long up = stats.length > 1 && stats[1] instanceof Long ? (Long) stats[1] : 0L;

        return total > 0 ? (up * 100.0) / total : 0.0;
    }

    public MonitorResponse getMonitorById(Long monitorId, String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        return MonitorResponse.builder()
                .id(monitor.getId())
                .name(monitor.getName())
                .url(monitor.getUrl())
                .checkFreq(monitor.getCheckFreq())
                .alertFrequencyMinutes(monitor.getAlertFrequencyMinutes())
                .httpMethod(monitor.getHttpMethod())
                .requestBody(monitor.getRequestBody())
                .isActive(monitor.isActive())
                .build();
    }

    public MonitorResponse updateMonitor(Long monitorId, String userEmail, MonitorRequest request) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        monitor.setName(request.getName());
        monitor.setUrl(request.getUrl());
        monitor.setCheckFreq(request.getCheckFreq());
        monitor.setAlertFrequencyMinutes(request.getAlertFrequencyMinutes());
        monitor.setHttpMethod(request.getHttpMethod());
        monitor.setRequestBody(request.getRequestBody());

        try {
            monitor.setHeadersJson(
                    request.getHeaders() != null
                            ? objectMapper.writeValueAsString(request.getHeaders())
                            : null
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to update headers JSON", e);
        }

        monitorRepository.save(monitor);
        return MonitorResponse.builder()
                .id(monitor.getId())
                .name(monitor.getName())
                .url(monitor.getUrl())
                .checkFreq(monitor.getCheckFreq())
                .isActive(monitor.isActive())
                .build();
    }
}