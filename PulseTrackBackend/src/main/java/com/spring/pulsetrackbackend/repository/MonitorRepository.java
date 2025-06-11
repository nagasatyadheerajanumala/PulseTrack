package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    List<Monitor> findByUser(User user);
}
