package com.spring.pulsetrackbackend.repository;

import com.spring.pulsetrackbackend.model.StatusPage;
import com.spring.pulsetrackbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusPageRepository extends JpaRepository<StatusPage, Long> {

    List<StatusPage> findByUser(User user);

    Optional<StatusPage> findByPublicKey(String publicKey);
    Optional<StatusPage> findByIdAndUser(Long id, User user);
}