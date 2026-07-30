package com.carereach.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
