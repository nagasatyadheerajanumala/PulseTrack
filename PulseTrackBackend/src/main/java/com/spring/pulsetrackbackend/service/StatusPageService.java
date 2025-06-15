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
import org.springframework.transaction.annotation.Transactional;

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
            Double uptime = total>0? (upCount*100.0) / total: null;
            monitor.setUptimePercent(uptime);

            // Set transient uptime field for badge use
            monitor.setUptimePercent(uptime);

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

    @Transactional
    public void deleteStatusPage(Long id, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        StatusPage page = statusPageRepo.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Not found"));

        page.getMonitors().clear(); // remove association
        statusPageRepo.delete(page);
    }

    public StatusPage getStatusPageEntity(String publicKey) {
        return statusPageRepo.findByPublicKey(publicKey)
                .orElseThrow(() -> new RuntimeException("Status page not found"));
    }

    public String generateBadgeSvg(String publicKey) {
        StatusPage page = getStatusPageEntity(publicKey);
        LocalDateTime since = LocalDateTime.now().minusDays(1);

        // Set uptime percent transiently so we can compute avg
        for (Monitor monitor : page.getMonitors()) {
            Object[] stats = monitorLogRepo.getUptimeStats(monitor, since);
            long total = (stats[0] instanceof Long) ? (Long) stats[0] : 0L;
            long upCount = (stats.length > 1 && stats[1] instanceof Long) ? (Long) stats[1] : 0L;
            Double uptime = total > 0 ? (upCount * 100.0) / total : 0.0;
            monitor.setUptimePercent(uptime);
        }

        double avgUptime = page.getMonitors().stream()
                .filter(m -> m.getUptimePercent() != null)
                .mapToDouble(Monitor::getUptimePercent)
                .average()
                .orElse(0.0);

        String color = avgUptime >= 99 ? "brightgreen" :
                avgUptime >= 95 ? "yellow" :
                        avgUptime > 0 ? "orange" : "red";

        String label = "uptime";
        String value = String.format("%.2f%%", avgUptime);

        return """
        <svg xmlns="http://www.w3.org/2000/svg" width="150" height="20">
          <linearGradient id="a" x2="0" y2="100%%">
            <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
            <stop offset="1" stop-opacity=".1"/>
          </linearGradient>
          <rect rx="3" width="150" height="20" fill="#555"/>
          <rect rx="3" x="60" width="90" height="20" fill="%s"/>
          <path fill="%s" d="M60 0h4v20h-4z"/>
          <rect rx="3" width="150" height="20" fill="url(#a)"/>
          <g fill="#fff" text-anchor="middle"
             font-family="Verdana,Geneva,DejaVu Sans,sans-serif" font-size="11">
            <text x="30" y="15">%s</text>
            <text x="105" y="15">%s</text>
          </g>
        </svg>
        """.formatted(color, color, label, value);
    }
}