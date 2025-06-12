package com.spring.pulsetrackbackend.controller;

import com.spring.pulsetrackbackend.dto.StatusPageRequest;
import com.spring.pulsetrackbackend.dto.StatusPageResponse;
import com.spring.pulsetrackbackend.service.StatusPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/status-pages")
@RequiredArgsConstructor
public class StatusPageController {

    private final StatusPageService statusPageService;

    @PostMapping
    public ResponseEntity<StatusPageResponse> createStatusPage(@RequestBody StatusPageRequest request,
                                                               Principal principal) {
        System.out.println("🔐 Reached StatusPageController - User: " + principal.getName());
        return ResponseEntity.ok(statusPageService.createStatusPage(principal.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<StatusPageResponse>> getUserStatusPages(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(statusPageService.getUserStatusPages(email));
    }

    @GetMapping("/public/{publicKey}")
    public ResponseEntity<StatusPageResponse> getPublicStatusPage(@PathVariable String publicKey) {
        return ResponseEntity.ok(statusPageService.getPublicStatusPage(publicKey));
    }
}