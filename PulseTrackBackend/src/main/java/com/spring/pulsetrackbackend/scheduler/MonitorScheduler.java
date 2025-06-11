package com.spring.pulsetrackbackend.scheduler;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitorScheduler {

    private final MonitorRepository monitorRepo;
    private final MonitorLogService logService;

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void runChecks() {
        List<Monitor> monitors = monitorRepo.findAll();

        for (Monitor monitor : monitors) {
            if (!monitor.isActive()) continue;

            try {
                long start = System.currentTimeMillis();
                HttpURLConnection connection = (HttpURLConnection) new URL(monitor.getUrl()).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // optional
                connection.connect();

                int statusCode = connection.getResponseCode();
                long responseTime = System.currentTimeMillis() - start;

                log.info("Checked {}: status={}, time={}ms", monitor.getUrl(), statusCode, responseTime);

                logService.saveLogWithRetry(monitor.getId());

            } catch (Exception e) {
                log.warn("Monitor failed: " + monitor.getUrl(), e);
                logService.saveLogWithRetry(monitor.getId());
            }
        }
    }
}