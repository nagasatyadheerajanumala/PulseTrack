package com.spring.pulsetrackbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.pulsetrackbackend.dto.MonitorLogResponse;
import com.spring.pulsetrackbackend.model.Alert;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.repository.AlertRepository;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorLogService {

    private final MonitorLogRepository monitorLogRepo;
    private final MonitorRepository monitorRepo;
    private final AlertRepository alertRepo;
    private final EmailService emailService;

    @Value("${alert.delay.minutes:15}")
    private int alertDelayMinutes;

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


    public void saveLogWithRetry(Long monitorId) {
        Monitor monitor = monitorRepo.findById(monitorId)
                .orElseThrow(() -> new IllegalArgumentException("Monitor not found"));

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        int maxRetries = 3;
        int statusCode = 0;
        long responseTime = -1;
        String responseBody = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                HttpMethod method = monitor.getHttpMethod() != null
                        ? HttpMethod.valueOf(monitor.getHttpMethod().toUpperCase())
                        : HttpMethod.GET;

                HttpHeaders headers = new HttpHeaders();
                if (monitor.getHeadersJson() != null && !monitor.getHeadersJson().isEmpty()) {
                    Map<String, String> headerMap = mapper.readValue(
                            monitor.getHeadersJson(), new TypeReference<>() {});
                    headerMap.forEach(headers::set);
                }

                HttpEntity<String> entity = new HttpEntity<>(monitor.getRequestBody(), headers);

                long start = System.currentTimeMillis();
                ResponseEntity<String> response = restTemplate.exchange(
                        monitor.getUrl(),
                        method,
                        entity,
                        String.class
                );
                responseTime = System.currentTimeMillis() - start;
                statusCode = response.getStatusCodeValue();
                responseBody = response.getBody();

                // 🛑 Check for body-level errors (even if statusCode is 200)
                boolean bodyHasError = responseBody != null &&
                        (responseBody.toLowerCase().contains("invalid api key")
                                || responseBody.toLowerCase().contains("\"error\"")
                                || responseBody.toLowerCase().contains("unauthorized")
                                || responseBody.toLowerCase().contains("forbidden"));

                if (!bodyHasError && statusCode == 200) break;

                if (bodyHasError) {
                    statusCode = 403; // Override status code to indicate failure
                }

            } catch (Exception e) {
                statusCode = 0;
                responseTime = -1;
            }
        }

        // Save log
        MonitorLog log = MonitorLog.builder()
                .monitor(monitor)
                .statusCode(statusCode)
                .responseTime(responseTime)
                .checkedAt(LocalDateTime.now())
                .build();
        monitorLogRepo.save(log);

        // Alert logic
        boolean isDown = statusCode != 200;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSent = monitor.getLastAlertSentAt();
        boolean shouldAlert = lastSent == null || Duration.between(lastSent, now).toMinutes() >= alertDelayMinutes;

        if (isDown && shouldAlert) {
            Alert alert = Alert.builder()
                    .monitor(monitor)
                    .message("Monitor \"" + monitor.getName() + "\" is down. Status code: " + statusCode)
                    .createdAt(now)
                    .resolved(false)
                    .build();
            alertRepo.save(alert);

            emailService.sendAlert(monitor.getUser().getEmail(), monitor, statusCode, responseTime);

            monitor.setLastAlertSentAt(now);
            monitorRepo.save(monitor);
        }
    }
}