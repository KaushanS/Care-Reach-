package com.carereach.backend.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "doctor_career")
public class DoctorCareer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private String qualification;

    @Column(nullable = false)
    private Integer experienceYears;
}
