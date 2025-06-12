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
    private List<Monitor> monitors;

    @ManyToOne
    private User user;
}