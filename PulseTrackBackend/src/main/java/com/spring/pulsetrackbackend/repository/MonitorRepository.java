package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.Monitor;
import com.spring.pulsetrackbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    List<Monitor> findByUser(User user);
    Optional<Monitor> findByIdAndUser(Long id, User user);

}
