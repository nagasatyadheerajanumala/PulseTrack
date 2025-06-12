package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MonitorResponse {
    private Long id;
    private String name;
    private String url;
    private int checkFreq;
    private boolean isActive;
    private Double uptimePercent;
    private Long averageResponseTime;
    private LocalDateTime lastChecked;
    private Integer lastStatusCode;
}
