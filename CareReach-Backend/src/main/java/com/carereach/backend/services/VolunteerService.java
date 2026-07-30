package com.carereach.backend.services;

import com.carereach.backend.dtos.PatientReportSummaryDto;
import com.carereach.backend.dtos.VolunteerDashboardDto;
import com.carereach.backend.models.Patient;
import com.carereach.backend.models.User;
import com.carereach.backend.repositories.PatientRepository;
import com.carereach.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolunteerService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public VolunteerDashboardDto getDashboardMetrics(Long volunteerId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        long totalReported = patientRepository.countByReportedById(volunteerId);
        long pendingVerify = patientRepository.countByReportedByIdAndStatus(volunteerId, "PENDING_VERIFY");
        long verified = patientRepository.countByReportedByIdAndStatus(volunteerId, "VERIFIED");

        List<Patient> recentPatientReports = patientRepository.findTop5ByReportedByIdOrderByIdDesc(volunteerId);
        List<PatientReportSummaryDto> recentReports = recentPatientReports.stream().map(p -> {
            PatientReportSummaryDto dto = new PatientReportSummaryDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            dto.setAddress(p.getAddress());
            dto.setNic(p.getNic());
            dto.setGuardianName(p.getGuardianName());
            dto.setGuardianPhone(p.getGuardianPhone());
            dto.setDistrict(p.getDistrict());
            dto.setDivisionalSecretariat(p.getDivisionalSecretariat());
            dto.setGnDivision(p.getGnDivision());
            dto.setDescription(p.getDescription());
            dto.setGpsCoordinates(p.getGpsCoordinates());
            dto.setStatus(p.getStatus());
            dto.setCreatedAt(p.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        String activeSinceString = calculateActiveSince(volunteer.getCreatedAt());

        VolunteerDashboardDto dashboardDto = new VolunteerDashboardDto();
        dashboardDto.setTotalReported(totalReported);
        dashboardDto.setPendingVerify(pendingVerify);
        dashboardDto.setVerified(verified);
        dashboardDto.setRecentReports(recentReports);
        dashboardDto.setActiveSince(activeSinceString);

        return dashboardDto;
    }

    public List<PatientReportSummaryDto> getAllReportsByVolunteer(Long volunteerId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("Volunteer ID cannot be null");
        }
        userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        List<Patient> allPatientReports = patientRepository.findAllByReportedByIdOrderByIdDesc(volunteerId);

        return allPatientReports.stream().map(p -> {
            PatientReportSummaryDto dto = new PatientReportSummaryDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            dto.setAddress(p.getAddress());
            dto.setNic(p.getNic());
            dto.setGuardianName(p.getGuardianName());
            dto.setGuardianPhone(p.getGuardianPhone());
            dto.setDistrict(p.getDistrict());
            dto.setDivisionalSecretariat(p.getDivisionalSecretariat());
            dto.setGnDivision(p.getGnDivision());
            dto.setDescription(p.getDescription());
            dto.setGpsCoordinates(p.getGpsCoordinates());
            dto.setStatus(p.getStatus());
            dto.setCreatedAt(p.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    private String calculateActiveSince(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "0 days";
        }
        long days = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        if (days < 30) {
            return days + " days";
        } else if (days < 365) {
            long months = days / 30;
            return months + " mo";
        } else {
            long years = days / 365;
            return years + " yr";
        }
    }
}
