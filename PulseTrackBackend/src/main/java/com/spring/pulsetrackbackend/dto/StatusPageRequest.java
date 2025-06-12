package com.spring.pulsetrackbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatusPageRequest {
    private String name;
    private List<Long> monitorIds;
}