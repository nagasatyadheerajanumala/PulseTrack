package com.spring.pulsetrackbackend.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitorPingScheduler {

    private final MonitorRepository monitorRepo;
    private final MonitorLogService monitorLogService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void checkMonitors() {
        List<Monitor> monitors = monitorRepo.findAll();

        for (Monitor monitor : monitors) {
            if (!monitor.isActive()) continue;

            LocalDateTime lastPing = monitor.getLastPingAt() != null
                    ? monitor.getLastPingAt()
                    : LocalDateTime.MIN;

            long elapsedMinutes = Duration.between(lastPing, LocalDateTime.now()).toMinutes();

            if (elapsedMinutes >= monitor.getCheckFreq()) {
                pingAndLog(monitor);
            }
        }
    }

    private void pingAndLog(Monitor monitor) {
        int statusCode;
        long responseTime;

        try {
            HttpMethod method = monitor.getHttpMethod() != null
                    ? HttpMethod.valueOf(monitor.getHttpMethod().toUpperCase())
                    : HttpMethod.GET;

            HttpHeaders headers = new HttpHeaders();
            if (monitor.getHeadersJson() != null && !monitor.getHeadersJson().isEmpty()) {
                Map<String, String> headerMap = objectMapper.readValue(
                        monitor.getHeadersJson(), new TypeReference<>() {});
                headerMap.forEach(headers::set);
            }

            HttpEntity<String> request = new HttpEntity<>(monitor.getRequestBody(), headers);

            long start = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.exchange(
                    monitor.getUrl(),
                    method,
                    request,
                    String.class
            );

            responseTime = System.currentTimeMillis() - start;
            String body = response.getBody();
            statusCode = response.getStatusCodeValue();

            // Check for body-level error
            boolean bodyHasError = body != null && (
                    body.toLowerCase().contains("invalid api key") ||
                            body.toLowerCase().contains("\"error\"") ||
                            body.toLowerCase().contains("unauthorized") ||
                            body.toLowerCase().contains("forbidden")
            );

            if (bodyHasError && statusCode == 200) {
                log.warn("Detected error in response body from {}: {}", monitor.getUrl(), body);
                statusCode = 403; // override
            }

        } catch (Exception e) {
            log.error("Error pinging {}: {}", monitor.getUrl(), e.getMessage());
            statusCode = 0;
            responseTime = -1;
        }

        // Save log and update last ping time
        monitorLogService.saveLog(monitor.getId(), statusCode, responseTime);
        monitor.setLastPingAt(LocalDateTime.now());
        monitorRepo.save(monitor); // persist updated ping timestamp

        log.info("Pinged {} - status: {}, time: {}ms", monitor.getUrl(), statusCode, responseTime);

        // Broadcast via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/status/" + monitor.getId(),
                Map.of(
                        "monitorId", monitor.getId(),
                        "statusCode", statusCode,
                        "responseTime", responseTime,
                        "checkedAt", LocalDateTime.now().toString()
                )
        );
    }
}