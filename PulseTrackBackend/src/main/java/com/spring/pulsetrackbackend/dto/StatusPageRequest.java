package com.spring.pulsetrackbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatusPageRequest {
    private String name;               // ✅ This matches getName()
    private List<Long> monitorIds;
}