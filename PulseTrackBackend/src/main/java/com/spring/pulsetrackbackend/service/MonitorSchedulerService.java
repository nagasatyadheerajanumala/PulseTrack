package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MonitorSchedulerService {
    private final MonitorRepository monitorRepository;
    private final MonitorLogRepository monitorLogRepo;

    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    @PostConstruct
    public void init(){
        scheduler.setPoolSize(10);
        scheduler.initialize();

        for(Monitor monitor : monitorRepository.findAll()){
            if(monitor.isActive()){
                scheduleMonitor(monitor);
            }
        }
    }

    public void scheduleMonitor(Monitor monitor) {
        Runnable task = () -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                long start = System.currentTimeMillis();
                var response = restTemplate.getForEntity(new URI(monitor.getUrl()), String.class);
                long duration = System.currentTimeMillis() - start;

                MonitorLog log = MonitorLog.builder()
                        .statusCode(response.getStatusCode().value())
                        .responseTime(duration)
                        .checkedAt(LocalDateTime.now())
                        .monitor(monitor)
                        .build();

                monitorLogRepo.save(log);
            } catch (Exception e) {
                MonitorLog log = MonitorLog.builder()
                        .statusCode(0) // 0 = error
                        .responseTime(0)
                        .checkedAt(LocalDateTime.now())
                        .monitor(monitor)
                        .build();

                monitorLogRepo.save(log);
            }
        };

        long frequencyMs = TimeUnit.SECONDS.toMillis(monitor.getCheckFreq());
        scheduler.scheduleAtFixedRate(task, frequencyMs);
    }

}
