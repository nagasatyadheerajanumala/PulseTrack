package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorLogRepository extends JpaRepository<MonitorLog, Long> {
    List<MonitorLog> findByMonitor(Monitor monitor);
}