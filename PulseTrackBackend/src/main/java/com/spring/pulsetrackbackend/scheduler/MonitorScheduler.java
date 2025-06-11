package com.spring.pulsetrackbackend.scheduler;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonitorScheduler {

    private final MonitorRepository monitorRepo;
    private final MonitorLogService logService;

    @PostConstruct
    public void init() {
        System.out.println("✅ Monitor scheduler initialized");
    }

    @Scheduled(fixedDelay = 60000) // every 60 seconds
    public void checkMonitors() {
        List<Monitor> activeMonitors = monitorRepo.findAll()
                .stream()
                .filter(Monitor::isActive)
                .toList();

        for (Monitor monitor : activeMonitors) {
            try {
                long start = System.currentTimeMillis();

                HttpURLConnection connection = (HttpURLConnection) new URL(monitor.getUrl()).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.connect();

                int statusCode = connection.getResponseCode();
                long responseTime = System.currentTimeMillis() - start;

                logService.saveLog(monitor.getId(), statusCode, responseTime);
            } catch (Exception e) {
                logService.saveLog(monitor.getId(), 500, 0);
            }
        }
    }
}