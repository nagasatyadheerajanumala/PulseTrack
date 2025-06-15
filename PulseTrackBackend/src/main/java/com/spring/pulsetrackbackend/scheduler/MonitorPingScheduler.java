package com.spring.pulsetrackbackend.scheduler;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonitorPingScheduler {

    private final MonitorRepository monitorRepo;
    private final MonitorLogService monitorLogService;
    private final SimpMessagingTemplate messagingTemplate;

    // Keeps track of last ping time for each monitor
    private final Map<Long, LocalDateTime> lastPingTimes = new HashMap<>();

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void checkMonitors() {
        for (Monitor monitor : monitorRepo.findAll()) {
            if (!monitor.isActive()) continue;

            Long id = monitor.getId();
            LocalDateTime lastPing = lastPingTimes.getOrDefault(id, LocalDateTime.MIN);
            long elapsedMinutes = Duration.between(lastPing, LocalDateTime.now()).toMinutes();

            if (elapsedMinutes >= monitor.getCheckFreq()) {
                pingAndLog(monitor);
                lastPingTimes.put(id, LocalDateTime.now());
            }
        }
    }

    private void pingAndLog(Monitor monitor) {
        try {
            long start = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) new URL(monitor.getUrl()).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.connect();
            int statusCode = conn.getResponseCode();
            long responseTime = System.currentTimeMillis() - start;

            monitorLogService.saveLog(monitor.getId(), statusCode, responseTime);
            log.info("Pinged {} - status: {}, time: {}ms", monitor.getUrl(), statusCode, responseTime);

            // 🚀 Broadcast real-time update
            messagingTemplate.convertAndSend(
                    "/topic/status/" + monitor.getId(),
                    Map.of(
                            "monitorId", monitor.getId(),
                            "statusCode", statusCode,
                            "responseTime", responseTime,
                            "checkedAt", LocalDateTime.now().toString()
                    )
            );

        } catch (Exception e) {
            log.error("Error pinging {}: {}", monitor.getUrl(), e.getMessage());
            monitorLogService.saveLog(monitor.getId(), 0, -1);

            messagingTemplate.convertAndSend(
                    "/topic/status/" + monitor.getId(),
                    Map.of(
                            "monitorId", monitor.getId(),
                            "statusCode", 0,
                            "responseTime", -1,
                            "checkedAt", LocalDateTime.now().toString()
                    )
            );
        }
    }
}