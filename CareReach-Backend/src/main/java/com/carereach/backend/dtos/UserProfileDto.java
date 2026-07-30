package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String nic;
    private String phone;
    private String address;
    private String role;
    private String profilePhoto;
    private String district;
    private String divisionalSecretariat;
    private String gnDivision;
    private String specialistCategory;
    private String hospitalName;
    private String qualification;
    private Integer experienceYears;
}
