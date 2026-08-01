package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class AdminVolunteerDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String nic;
    private String address;
    private String divisionalSecretariat;
    private String district;
    private String gnDivision;
    private String profilePhoto;
    private long reportedPatientsCount;
}
