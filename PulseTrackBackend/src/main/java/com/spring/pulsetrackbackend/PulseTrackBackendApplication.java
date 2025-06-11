package com.spring.pulsetrackbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PulseTrackBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PulseTrackBackendApplication.class, args);
    }
}
