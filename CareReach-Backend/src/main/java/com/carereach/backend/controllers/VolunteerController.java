package com.carereach.backend.controllers;

import com.carereach.backend.dtos.VolunteerDashboardDto;
import com.carereach.backend.services.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/dashboard/{volunteerId}")
    public ResponseEntity<?> getDashboardMetrics(@PathVariable Long volunteerId) {
        try {
            VolunteerDashboardDto dto = volunteerService.getDashboardMetrics(volunteerId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{volunteerId}/reports")
    public ResponseEntity<?> getAllVolunteerReports(@PathVariable Long volunteerId) {
        try {
            java.util.List<com.carereach.backend.dtos.PatientReportSummaryDto> reports = volunteerService
                    .getAllReportsByVolunteer(volunteerId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
