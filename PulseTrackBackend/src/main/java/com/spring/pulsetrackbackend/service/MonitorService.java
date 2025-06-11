package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.MonitorRequest;
import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.User;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final UserRepository userRepo;

    public MonitorResponse createMonitor(String userEmail, MonitorRequest request) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();

        Monitor monitor = Monitor.builder()
                .name(request.getName())
                .url(request.getUrl())
                .checkFreq(request.getCheckFreq())
                .alertFrequencyMinutes(request.getAlertFrequencyMinutes()) // 🆕
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        monitorRepository.save(monitor);

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

    public void toggleMonitorStatus(Long monitorId, String email) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor not found"));

        if (!monitor.getUser().getEmail().equals(email)) {
            throw new SecurityException("Unauthorized to modify this monitor.");
        }

        monitor.setActive(!monitor.isActive());
        monitorRepository.save(monitor);
    }
}