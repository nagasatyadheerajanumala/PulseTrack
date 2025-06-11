package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitorResponse {
    private Long id;
    private String name;
    private String url;
    private int checkFreq;
    private boolean isActive;
}
