package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.dto.StatusPageRequest;
import com.spring.pulsetrackbackend.dto.StatusPageResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import com.spring.pulsetrackbackend.model.StatusPage;
import com.spring.pulsetrackbackend.model.User;
import com.spring.pulsetrackbackend.repository.MonitorLogRepository;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.repository.StatusPageRepository;
import com.spring.pulsetrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusPageService {

    private final StatusPageRepository statusPageRepo;
    private final MonitorRepository monitorRepo;
    private final UserRepository userRepo;
    private final MonitorLogRepository monitorLogRepo;

    public StatusPageResponse createStatusPage(String userEmail, StatusPageRequest request) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        List<Monitor> monitors = monitorRepo.findAllById(request.getMonitorIds());

        StatusPage statusPage = StatusPage.builder()
                .name(request.getName())
                .user(user)
                .publicKey(UUID.randomUUID().toString())
                .monitors(monitors)
                .build();

        statusPageRepo.save(statusPage);
        return mapToResponse(statusPage);
    }

    public List<StatusPageResponse> getUserStatusPages(String userEmail) {
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        return statusPageRepo.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StatusPageResponse getPublicStatusPage(String publicKey) {
        StatusPage page = statusPageRepo.findByPublicKey(publicKey)
                .orElseThrow(() -> new RuntimeException("Public status page not found"));
        return mapToResponse(page);
    }

    private StatusPageResponse mapToResponse(StatusPage page) {
        LocalDateTime since = LocalDateTime.now().minusDays(1);

        List<MonitorResponse> monitorResponses = page.getMonitors().stream().map(monitor -> {
            Object[] stats = monitorLogRepo.getUptimeStats(monitor, since);
            long total = (stats[0] instanceof Long) ? (Long) stats[0] : 0L;
            long upCount = (stats.length > 1 && stats[1] instanceof Long) ? (Long) stats[1] : 0L;
            Double uptime = total > 0 ? (upCount * 100.0) / total : null;

            List<MonitorLog> logs = monitorLogRepo.findByMonitor(monitor);
            long avgResponse = logs.isEmpty() ? 0 :
                    logs.stream().mapToLong(MonitorLog::getResponseTime).sum() / logs.size();

            MonitorLog lastLog = monitorLogRepo.findTopByMonitorOrderByCheckedAtDesc(monitor);

            return MonitorResponse.builder()
                    .id(monitor.getId())
                    .name(monitor.getName())
                    .url(monitor.getUrl())
                    .checkFreq(monitor.getCheckFreq())
                    .isActive(monitor.isActive())
                    .uptimePercent(uptime)
                    .averageResponseTime(avgResponse)
                    .lastChecked(lastLog != null ? lastLog.getCheckedAt() : null)
                    .lastStatusCode(lastLog != null ? lastLog.getStatusCode() : null)
                    .build();
        }).collect(Collectors.toList());

        return StatusPageResponse.builder()
                .id(page.getId())
                .name(page.getName())
                .publicKey(page.getPublicKey())
                .monitors(monitorResponses)
                .build();
    }
}