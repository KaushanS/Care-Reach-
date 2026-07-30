package com.carereach.backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class VolunteerDashboardDto {
    private long totalReported;
    private long pendingVerify;
    private long verified;
    private String activeSince;
    private List<PatientReportSummaryDto> recentReports;
}
