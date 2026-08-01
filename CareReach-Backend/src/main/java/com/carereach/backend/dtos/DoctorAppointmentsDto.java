package com.carereach.backend.dtos;

import java.util.List;
import java.util.Map;

public class DoctorAppointmentsDto {
    private long totalToday;
    private long pendingToday;
    private long completedToday;

    private List<Map<String, Object>> upcomingVisits;
    private List<Map<String, Object>> completedVisits;

    public long getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(long totalToday) {
        this.totalToday = totalToday;
    }

    public long getPendingToday() {
        return pendingToday;
    }

    public void setPendingToday(long pendingToday) {
        this.pendingToday = pendingToday;
    }

    public long getCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(long completedToday) {
        this.completedToday = completedToday;
    }

    public List<Map<String, Object>> getUpcomingVisits() {
        return upcomingVisits;
    }

    public void setUpcomingVisits(List<Map<String, Object>> upcomingVisits) {
        this.upcomingVisits = upcomingVisits;
    }

    public List<Map<String, Object>> getCompletedVisits() {
        return completedVisits;
    }

    public void setCompletedVisits(List<Map<String, Object>> completedVisits) {
        this.completedVisits = completedVisits;
    }
}
