package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String profilePhoto;
    private String nic;
    private String gnDivision;
    private String specialistCategory;
    private String hospitalName;
    private String qualification;
    private Integer experienceYears;
}
