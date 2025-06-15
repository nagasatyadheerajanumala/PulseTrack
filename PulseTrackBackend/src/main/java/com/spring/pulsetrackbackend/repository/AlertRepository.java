package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.Alert;
import com.spring.pulsetrackbackend.model.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByMonitorUserEmailAndResolved(String email, boolean resolved);
    void deleteByMonitor(Monitor monitor);
}