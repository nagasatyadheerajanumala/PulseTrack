package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.dto.AlertResponse;
import com.spring.pulsetrackbackend.model.Alert;
import com.spring.pulsetrackbackend.model.User;
import com.spring.pulsetrackbackend.repository.AlertRepository;
import com.spring.pulsetrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepo;
    private final UserRepository userRepo;

    public List<AlertResponse> getUserAlerts(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Alert> alerts = alertRepo.findAll()
                .stream()
                .filter(a -> a.getMonitor().getUser().equals(user))
                .collect(Collectors.toList());

        return alerts.stream().map(alert -> AlertResponse.builder()
                .id(alert.getId())
                .message(alert.getMessage())
                .createdAt(alert.getCreatedAt())
                .resolved(alert.isResolved())
                .build()).collect(Collectors.toList());
    }
}