package com.carereach.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer age;

    private String gender;

    @Column(unique = true)
    private String nic;

    @Column(unique = true)
    private String patientEmail;

    private String patientPhone;

    private String guardianName;

    private String guardianPhone;

    private String guardianEmail;

    private String address;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean hasAccount = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean hasGuardianAccount = false;

    private String gnDivision;

    private String district;

    private String divisionalSecretariat;

    private String gpsCoordinates;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @CreationTimestamp
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime updatedAt;
}
