package com.carereach.backend.dtos;

import java.util.List;
import java.util.Map;

public class DoctorRegistryDto {
    private long totalPatients;
    private List<Map<String, Object>> activePatients;

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public List<Map<String, Object>> getActivePatients() {
        return activePatients;
    }

    public void setActivePatients(List<Map<String, Object>> activePatients) {
        this.activePatients = activePatients;
    }
}
