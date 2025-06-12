package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusAnalyticsResponse {
    private int totalChecks;
    private int totalFailures;
    private double uptimePercentage;
}