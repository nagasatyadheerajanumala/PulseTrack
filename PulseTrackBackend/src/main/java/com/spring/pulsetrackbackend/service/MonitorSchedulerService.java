package com.spring.pulsetrackbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MonitorSchedulerService {

    private final MonitorRepository monitorRepository;
    private final MonitorLogRepository monitorLogRepo;
    private final ObjectMapper objectMapper;

    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        scheduler.setPoolSize(10);
        scheduler.initialize();

        for (Monitor monitor : monitorRepository.findAll()) {
            if (monitor.isActive()) {
                scheduleMonitor(monitor);
            }
        }
    }

    public void scheduleMonitor(Monitor monitor) {
        Long monitorId = monitor.getId();

        Runnable task = () -> {
            try {
                Monitor dbMonitor = monitorRepository.findById(monitorId).orElse(null);
                if (dbMonitor == null || !dbMonitor.isActive()) return;

                RestTemplate restTemplate = new RestTemplate();

                HttpMethod method = dbMonitor.getHttpMethod() != null
                        ? HttpMethod.valueOf(dbMonitor.getHttpMethod().toUpperCase())
                        : HttpMethod.GET;

                HttpHeaders headers = new HttpHeaders();

                if (dbMonitor.getHeadersJson() != null && !dbMonitor.getHeadersJson().isEmpty()) {
                    Map<String, String> headerMap = objectMapper.readValue(
                            dbMonitor.getHeadersJson(), new TypeReference<>() {});
                    headerMap.forEach(headers::set);
                }

                HttpEntity<String> request = new HttpEntity<>(
                        dbMonitor.getRequestBody(), headers
                );

                long start = System.currentTimeMillis();

                ResponseEntity<String> response = restTemplate.exchange(
                        new URI(dbMonitor.getUrl()),
                        method,
                        request,
                        String.class
                );

                long duration = System.currentTimeMillis() - start;

                int statusCode = response.getStatusCodeValue();
                String responseBody = response.getBody();

// 🔍 Look for common API error messages
                boolean bodyIndicatesError = responseBody != null &&
                        (responseBody.toLowerCase().contains("invalid api key")
                                || responseBody.toLowerCase().contains("\"error\"")
                                || responseBody.toLowerCase().contains("unauthorized")
                                || responseBody.toLowerCase().contains("forbidden"));

                if (bodyIndicatesError) {
                    statusCode = 403; // or any other number to indicate auth failure
                }

                MonitorLog log = MonitorLog.builder()
                        .statusCode(statusCode)
                        .responseTime(duration)
                        .checkedAt(LocalDateTime.now())
                        .monitor(dbMonitor)
                        .build();

                monitorLogRepo.save(log);

            } catch (Exception e) {
                Monitor fallback = monitorRepository.findById(monitorId).orElse(null);
                if (fallback == null) return;

                MonitorLog errorLog = MonitorLog.builder()
                        .statusCode(0)
                        .responseTime(0)
                        .checkedAt(LocalDateTime.now())
                        .monitor(fallback)
                        .build();

                monitorLogRepo.save(errorLog);
            }
        };

        long frequencyMs = TimeUnit.SECONDS.toMillis(monitor.getCheckFreq());
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, frequencyMs);
        scheduledTasks.put(monitorId, future);
    }

    public void cancelMonitor(Long monitorId) {
        ScheduledFuture<?> future = scheduledTasks.remove(monitorId);
        if (future != null) {
            future.cancel(true);
        }
    }
}