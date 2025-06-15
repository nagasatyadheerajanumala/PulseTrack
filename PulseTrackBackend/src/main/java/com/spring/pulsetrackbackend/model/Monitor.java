package com.spring.pulsetrackbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.Transient;

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

    @ManyToMany(mappedBy = "monitors")
    private Set<StatusPage> statusPages;

    @Transient
    private Double uptimePercent;

    public Double getUptimePercent() {
        return uptimePercent;
    }

    public void setUptimePercent(Double uptimePercent) {
        this.uptimePercent = uptimePercent;
    }

}
