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

    public void resolveAlert(Long alertId, String email){
        Alert alert = alertRepo.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        if(!alert.getMonitor().getUser().getEmail().equals(email)){
            throw new RuntimeException("Unauthorized to resolve this alert");
        }
        alert.setResolved(true);
        alertRepo.save(alert);
    }

    public List<AlertResponse> getAlerts(String email, boolean resolved) {
        return alertRepo.findByMonitorUserEmailAndResolved(email, resolved).stream()
                .map(alert -> AlertResponse.builder()
                        .id(alert.getId())
                        .message(alert.getMessage())
                        .resolved(alert.isResolved())
                        .createdAt(alert.getCreatedAt())
                        .monitorId(alert.getMonitor().getId())
                        .monitorName(alert.getMonitor().getName())
                        .build())
                .collect(Collectors.toList());
    }


}