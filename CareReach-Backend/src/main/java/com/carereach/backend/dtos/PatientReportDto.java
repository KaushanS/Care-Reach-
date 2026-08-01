package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class PatientReportDto {
    private String name;
    private Integer age;
    private String gender;
    private String nic;
    private String patientEmail;
    private String patientPhone;

    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;

    private String address;
    private String district;
    private String divisionalSecretariat;
    private String gnDivision;
    private String gpsCoordinates;

    private String description;

    private Long reportedById;
}
