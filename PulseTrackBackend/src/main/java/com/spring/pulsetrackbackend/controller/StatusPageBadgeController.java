package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.service.StatusPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status-pages/public")
@RequiredArgsConstructor
public class StatusPageBadgeController {

    private final StatusPageService statusPageService;

    @GetMapping(value = "/{publicKey}/badge", produces = "image/svg+xml")
    public ResponseEntity<String> getBadge(@PathVariable String publicKey) {
        String svg = statusPageService.generateBadgeSvg(publicKey);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"uptime.svg\"")
                .body(svg);
    }
}