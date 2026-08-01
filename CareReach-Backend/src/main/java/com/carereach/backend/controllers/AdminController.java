package com.carereach.backend.controllers;

import com.carereach.backend.dtos.AdminDashboardDto;
import com.carereach.backend.dtos.AdminAnalyticsDto;
import com.carereach.backend.services.AdminService;
import com.carereach.backend.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.carereach.backend.dtos.OfficerRegistrationDto;
import com.carereach.backend.dtos.AdminDoctorDto;
import com.carereach.backend.services.AdminReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminReportService adminReportService;

    @GetMapping("/reports/patients/csv")
    public ResponseEntity<byte[]> getPatientCsv() {
        byte[] data = adminReportService.generatePatientCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient_demographics.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/reports/officers/csv")
    public ResponseEntity<byte[]> getOfficerCsv() {
        byte[] data = adminReportService.generateOfficerCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gn_officer_network.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/reports/users/csv")
    public ResponseEntity<byte[]> getUserAuditCsv() {
        byte[] data = adminReportService.generateUserAuditCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprehensive_user_audit.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/reports/emergencies/pdf")
    public ResponseEntity<byte[]> getEmergencyPdf() {
        byte[] data = adminReportService.generateEmergencyPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emergency_incident_logs.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/reports/patients/pdf")
    public ResponseEntity<byte[]> getPatientPdf() {
        byte[] data = adminReportService.generatePatientPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient_demographics.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/reports/officers/pdf")
    public ResponseEntity<byte[]> getOfficerPdf() {
        byte[] data = adminReportService.generateOfficerPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gn_officer_network.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/reports/users/pdf")
    public ResponseEntity<byte[]> getUserAuditPdf() {
        byte[] data = adminReportService.generateUserAuditPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprehensive_user_audit.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @GetMapping("/reports/emergencies/csv")
    public ResponseEntity<byte[]> getEmergencyCsv() {
        byte[] data = adminReportService.generateEmergencyCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emergency_incident_logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboardOverview() {
        AdminDashboardDto dto = adminService.getDashboardStats();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsDto> getAnalytics() {
        AdminAnalyticsDto dto = adminService.getAnalyticsData();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/officers")
    public ResponseEntity<List<User>> getAllOfficers() {
        List<User> officers = adminService.getAllOfficers();
        return ResponseEntity.ok(officers);
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<AdminDoctorDto>> getAllDoctors() {
        List<AdminDoctorDto> doctors = adminService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/guardians")
    public ResponseEntity<List<com.carereach.backend.dtos.AdminGuardianDto>> getAllGuardians() {
        List<com.carereach.backend.dtos.AdminGuardianDto> guardians = adminService.getAllGuardians();
        return ResponseEntity.ok(guardians);
    }

    @GetMapping("/volunteers")
    public ResponseEntity<List<com.carereach.backend.dtos.AdminVolunteerDto>> getAllVolunteers() {
        List<com.carereach.backend.dtos.AdminVolunteerDto> volunteers = adminService.getAllVolunteers();
        return ResponseEntity.ok(volunteers);
    }

    @PostMapping("/officers")
    public ResponseEntity<?> registerOfficer(@RequestBody OfficerRegistrationDto dto) {
        try {
            User savedUser = adminService.registerGNOfficer(dto);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
