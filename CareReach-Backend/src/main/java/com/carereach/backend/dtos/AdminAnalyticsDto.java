package com.carereach.backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AdminAnalyticsDto {
    // Chart 1: Patient registrations per month (label -> count)
    private List<String> monthlyLabels;
    private List<Long> monthlyCounts;

    // Chart 2: Patient status distribution
    private long verifiedCount;
    private long pendingCount;
    private long rejectedCount;

    // Chart 3: Patients by district (top 5)
    private List<String> districtLabels;
    private List<Long> districtCounts;

    // Chart 4: Users by role
    private long doctorCount;
    private long officerCount;
    private long guardianCount;
    private long volunteerCount;
    private long patientAccountCount;
}
