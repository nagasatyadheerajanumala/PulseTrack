package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonitorStatusDto {
    private Long id;
    private String name;
    private String url;
    private boolean isActive;
    private int checkFreq;
    private double uptimePercentage;
    private List<MonitorLogResponse> recentLogs;
}