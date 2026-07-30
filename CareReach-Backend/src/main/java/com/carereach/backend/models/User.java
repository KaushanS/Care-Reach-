package com.carereach.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true, unique = true)
    private String nic;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = true, length = 500)
    private String address;

    @Column(nullable = true)
    private String district;

    @Column(nullable = true)
    private String divisionalSecretariat;

    @Column(nullable = true)
    private String gnDivision;

    @Column(columnDefinition = "LONGTEXT")
    private String profilePhoto;

    @Column(nullable = true)
    private String specialistCategory;

    @Column(nullable = true)
    private String hospitalName;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean requiresCredentialReset = false;

    @Column(nullable = true)
    private String resetOtpCode;

    @Column(nullable = true)
    private java.time.LocalDateTime resetOtpExpiry;

    @CreationTimestamp
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime updatedAt;
}
