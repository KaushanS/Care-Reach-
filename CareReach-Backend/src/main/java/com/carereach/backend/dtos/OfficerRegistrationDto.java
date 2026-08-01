package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class OfficerRegistrationDto {
    private String fullName;
    private String nic;
    private String dob;
    private String gender;
    private String gnDivisionName;
    private String gnDivisionNumber;
    private String province;
    private String district;
    private String divisionalSecretariat;
    private String email;
    private String phone;
    private String address;
    private String username;
}
