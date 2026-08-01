package com.carereach.backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AdminAnalyticsDto {
    //Patient registrations per month 
    private List<String> monthlyLabels;
    private List<Long> monthlyCounts;

    //Patient status distribution
    private long verifiedCount;
    private long pendingCount;
    private long rejectedCount;

    //Patients by district 
    private List<String> districtLabels;
    private List<Long> districtCounts;

    //Users by role
    private long doctorCount;
    private long officerCount;
    private long guardianCount;
    private long volunteerCount;
    private long patientAccountCount;
}
