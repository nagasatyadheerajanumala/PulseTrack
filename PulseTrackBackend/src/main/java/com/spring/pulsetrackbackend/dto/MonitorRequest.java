package com.spring.pulsetrackbackend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MonitorRequest {
    private String name;
    private String url;
    private int checkFreq;
    private int alertFrequencyMinutes;
    private String httpMethod;
    private Map<String, String> headers;
    private String requestBody;
}
