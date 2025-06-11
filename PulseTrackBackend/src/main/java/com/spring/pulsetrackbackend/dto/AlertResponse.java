package com.spring.pulsetrackbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private boolean resolved;
}