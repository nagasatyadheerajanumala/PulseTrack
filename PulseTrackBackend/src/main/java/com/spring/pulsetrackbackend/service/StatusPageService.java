package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.MonitorResponse;
import com.spring.pulsetrackbackend.dto.StatusPageRequest;
import com.spring.pulsetrackbackend.dto.StatusPageResponse;
import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.StatusPage;
import com.spring.pulsetrackbackend.model.User;
import com.spring.pulsetrackbackend.repository.MonitorRepository;
import com.spring.pulsetrackbackend.repository.StatusPageRepository;
import com.spring.pulsetrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusPageService {

    private final StatusPageRepository statusPageRepo;
    private final MonitorRepository monitorRepo;
    private final UserRepository userRepo;

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
        return StatusPageResponse.builder()
                .id(page.getId())
                .name(page.getName())
                .publicKey(page.getPublicKey())
                .monitors(page.getMonitors().stream().map(m -> MonitorResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .url(m.getUrl())
                        .checkFreq(m.getCheckFreq())
                        .isActive(m.isActive())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}