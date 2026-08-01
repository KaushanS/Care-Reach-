package com.carereach.backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AdminDashboardDto {
    private long totalPatients;
    private long verifiedPatients;
    private long pendingPatients;
    private long rejectedPatients;
    private List<PatientReportSummaryDto> recentEmergencies;
}
