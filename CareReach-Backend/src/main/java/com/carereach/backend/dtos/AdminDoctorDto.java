package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class AdminDoctorDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String nic;
    private String username;
    private String address;
    private java.time.LocalDateTime createdAt;

    // Extracted DoctorCareer
    private String specialization;
    private String hospitalName;
    private String qualification;
    private Integer experienceYears;

    // Location Data
    private String district;
    private String divisionalSecretariat;

    private String profilePhoto;
}
