package com.spring.pulsetrackbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private int checkFreq;
    private boolean isActive;

    private LocalDateTime createdAt;
    @Column(name = "last_alert_sent_at")
    private LocalDateTime lastAlertSentAt;

    @Column(nullable = false)
    private int alertFrequencyMinutes;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
