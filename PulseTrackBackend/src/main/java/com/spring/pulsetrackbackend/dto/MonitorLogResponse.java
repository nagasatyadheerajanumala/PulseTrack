package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MonitorLogResponse {
    private Long id;
    private int statusCode;
    private long responseTime;
    private LocalDateTime checkedAt;
}