package com.spring.pulsetrackbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String publicKey;

    @ManyToMany
    @JoinTable(
            name = "status_pages_monitors", // plural to match your table
            joinColumns = @JoinColumn(name = "status_page_id"),
            inverseJoinColumns = @JoinColumn(name = "monitor_id")
    )
    private List<Monitor> monitors;

    @ManyToOne
    private User user;
}