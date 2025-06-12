package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatusPageResponse {
    private Long id;
    private String name;
    private String publicKey;
    private List<MonitorResponse> monitors;
}