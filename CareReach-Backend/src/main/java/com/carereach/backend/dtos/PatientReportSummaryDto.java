package com.carereach.backend.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PatientReportSummaryDto {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String address;
    private String nic;
    private String guardianName;
    private String guardianPhone;
    private String district;
    private String divisionalSecretariat;
    private String gnDivision;
    private String description;
    private String gpsCoordinates;
    private String status;
    private String reportedBy;
    private String reporterPhoto;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
