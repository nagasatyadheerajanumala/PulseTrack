package com.spring.pulsetrackbackend.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitorScheduler {

    private final MonitorRepository monitorRepo;
    private final MonitorLogService logService;
    private final ObjectMapper objectMapper; // For JSON -> Map
    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void runChecks() {
        List<Monitor> monitors = monitorRepo.findAll();

        for (Monitor monitor : monitors) {
            if (!monitor.isActive()) continue;

            try {
                HttpMethod method = monitor.getHttpMethod() != null
                        ? HttpMethod.valueOf(monitor.getHttpMethod().toUpperCase())
                        : HttpMethod.GET;

                HttpHeaders headers = new HttpHeaders();

                // Deserialize headers from JSON string
                if (monitor.getHeadersJson() != null && !monitor.getHeadersJson().isEmpty()) {
                    Map<String, String> headerMap = objectMapper.readValue(
                            monitor.getHeadersJson(), new TypeReference<>() {});
                    headerMap.forEach(headers::set);
                }

                HttpEntity<String> requestEntity = new HttpEntity<>(
                        monitor.getRequestBody(), headers
                );

                long start = System.currentTimeMillis();

                ResponseEntity<String> response = restTemplate.exchange(
                        monitor.getUrl(),
                        method,
                        requestEntity,
                        String.class
                );

                long responseTime = System.currentTimeMillis() - start;

                String body = response.getBody();
                boolean isError = body != null && body.contains("Invalid API key");

                int statusCode = isError ? 403 : response.getStatusCodeValue();

                log.info("Checked {} {}: status={}, time={}ms",
                        monitor.getHttpMethod(), monitor.getUrl(), statusCode, responseTime);

                logService.saveLogWithRetry(monitor.getId());

            } catch (Exception e) {
                log.warn("Monitor failed: " + monitor.getUrl(), e);
                logService.saveLogWithRetry(monitor.getId());
            }
        }
    }
}