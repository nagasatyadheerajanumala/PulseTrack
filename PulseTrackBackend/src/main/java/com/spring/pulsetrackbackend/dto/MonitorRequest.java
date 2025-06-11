package com.spring.pulsetrackbackend.dto;

import lombok.Data;

@Data
public class MonitorRequest {
    private String name;
    private String url;
    private int checkFreq;
    private int alertFrequencyMinutes;
}
