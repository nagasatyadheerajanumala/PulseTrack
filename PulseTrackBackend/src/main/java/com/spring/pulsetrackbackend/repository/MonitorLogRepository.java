package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.MonitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MonitorLogRepository extends JpaRepository<MonitorLog, Long> {
    @Query("SELECT COUNT(m), SUM(CASE WHEN m.statusCode = 200 THEN 1 ELSE 0 END) FROM MonitorLog m WHERE m.monitor = :monitor AND m.checkedAt >= :since")
    Object[] getUptimeStats(Monitor monitor, LocalDateTime since);

    MonitorLog findTopByMonitorOrderByCheckedAtDesc(Monitor monitor);
    List<MonitorLog> findByMonitor(Monitor monitor);
}