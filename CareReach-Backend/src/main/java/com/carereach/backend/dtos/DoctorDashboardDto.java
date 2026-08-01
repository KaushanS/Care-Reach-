package com.carereach.backend.dtos;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DoctorDashboardDto {
    private long pendingVerifiedVisits;
    private long completedVisits;
    private long registeredPatients;
    private List<Map<String, Object>> verifiedHomeVisits;
    private List<Map<String, Object>> todayAdded;
}
