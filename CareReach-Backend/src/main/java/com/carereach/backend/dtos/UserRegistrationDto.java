package com.carereach.backend.dtos;

import com.carereach.backend.models.Role;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String fullName;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String nic;
    private String phone;
    private String address;
    private String divisionalSecretariat;
    private String district;
    private String gnDivision;
    private Role role;
    private Long patientId;

    // Doctor specific fields
    private String specialization;
    private String hospitalName;
    private String qualification;
    private Integer experienceYears;
}
