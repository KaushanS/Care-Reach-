package com.carereach.backend.controllers;

import com.carereach.backend.dtos.MedicalReportDto;
import com.carereach.backend.services.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medical-reports")
public class MedicalReportController {

    @Autowired
    private MedicalReportService medicalReportService;

    @PostMapping
    public ResponseEntity<?> createMedicalReport(@RequestBody MedicalReportDto dto) {
        try {
            MedicalReportDto created = medicalReportService.createMedicalReport(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getReportsForPatient(@PathVariable Long patientId) {
        try {
            return ResponseEntity.ok(medicalReportService.getReportsByPatientId(patientId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
